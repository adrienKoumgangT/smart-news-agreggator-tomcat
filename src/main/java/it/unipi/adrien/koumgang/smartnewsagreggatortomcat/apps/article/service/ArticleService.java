package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.dao.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.repository.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.service.CommentService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionRequest;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReactionsResponse;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.service.ReactionService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.service.ServerEventLogService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.RedisInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.RessourceNotFound;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.EntityTypeEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service.BaseService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils.PaginateUtils;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ArticleService extends BaseService {

    @Contract(" -> new")
    public static @NotNull ArticleService getInstance() {
        return new ArticleService(ArticleRepository.getInstance());
    }



    private final ArticleDao articleDao;

    private final CommentService commentService = CommentService.getInstance();

    private final ReactionService reactionService = ReactionService.getInstance();

    public ArticleService(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }


    private static final int MAX_EMBEDDED_DEPTH = 2;


    @Contract(pure = true)
    private static @NotNull String formArticleKey(String id) {
        return "article:" + id;
    }

    @Contract(pure = true)
    private static @NotNull String formTagKey(String tagsName) {
        return "tag:" + tagsName;
    }

    @Contract(pure = true)
    private static @NotNull String formArticleByTagKey(String tagsName) {
        return "article:tag:" + tagsName + ":articles";
    }



    @Contract(pure = true)
    private static @NotNull String formCommentKey(String articleId) {
        return "article:comments:" + articleId;
    }

    @Contract(pure = true)
    private static @NotNull String formCommentKey(String articleId, int depth, int page, int size) {
        return "article:comments:" + articleId + ":depth:" + depth + ":page:" + page + ":size:" + size;
    }

    private void invalidateCommentCache(UserToken userToken, String articleId) throws Exception {
        String pattern = formCommentKey(articleId) + ":*";

        Set<String> keys = RedisInstance.getInstance().keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            for(String key : keys) {
                RedisInstance.getInstance().delete(key);
            }
        }
    }

    private String buildCommentsCacheKey(UserToken userToken, String articleId, int depth, int page, int size) {
        return formCommentKey(articleId, depth, page, size);
    }



    @Contract(pure = true)
    private static @NotNull String formReactionKey(String articleId) {
        return "article:reactions:" + articleId;
    }

    @Contract(pure = true)
    private static @NotNull String formReactionKey(UserToken userToken, String articleId, int page, int size) {
        return "article:reactions:" + articleId + ":page:" + page + ":size:" + size;
    }

    private void invalidateReactionCache(UserToken userToken, String articleId) throws Exception {
        String pattern = formReactionKey(articleId) + ":*";

        Set<String> keys = RedisInstance.getInstance().keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            for(String key : keys) {
                RedisInstance.getInstance().delete(key);
            }
        }
    }

    private String buildReactionCacheKey(UserToken userToken, String articleId, int depth, int page, int size) {
        return formCommentKey(articleId, depth, page, size);
    }





    public ArticleView getArticleById(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [GET] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Article> optArticle = articleDao.findById(objectId);

            if(optArticle.isEmpty()) {
                timePrinter.missing("Article not found");
                return null;
            }

            ArticleView articleView = new ArticleView(optArticle.get());

            timePrinter.log();

            return articleView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return null;
        }
    }

    public ArticleSimpleView getArticleSimpleById(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [GET] id: " + id);

        String articleKey = formArticleKey(id);

        try {
            ArticleSimpleView cacheData = RedisInstance.getInstance().get(articleKey, ArticleSimpleView.class);
            if(cacheData != null){
                timePrinter.log();
                return cacheData;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            MineLog.missing(e.getMessage());
        }

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Article> optArticle = articleDao.findById(objectId);

            if(optArticle.isEmpty()) {
                timePrinter.missing("Article not found");
                return null;
            }

            ArticleSimpleView articleView = new ArticleSimpleView(optArticle.get());

            try {
                RedisInstance.getInstance().set(articleKey, articleView, 60*10);
            } catch (Exception e) {
                // e.printStackTrace();
                MineLog.error(e.getMessage());
            }

            timePrinter.log();

            return articleView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return null;
        }
    }

    public List<ArticleView> listArticles(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [LIST] ");

        List<Article> articles = articleDao.findAll();

        List<ArticleView> articleViews = articles.stream().map(ArticleView::new).toList();

        timePrinter.log();

        return articleViews;
    }

    public List<ArticleView> listArticles(UserToken userToken, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [LIST] page: " + page + ", size: " + pageSize
        );

        List<Article> articles = articleDao.findAll(page, pageSize);

        List<ArticleView> articleViews = articles.stream().map(ArticleView::new).toList();

        timePrinter.log();

        return articleViews;
    }

    public long count(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [COUNT] ");

        long count = articleDao.count();

        timePrinter.log();

        return count;
    }

    public ArticleView saveArticle(UserToken userToken, ArticleView articleDetails) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [SAVE] " + gson.toJson(articleDetails)
        );

        try {
            Article article = new Article(articleDetails);
            ObjectId articleId = articleDao.save(article);

            if(articleId == null) {
                timePrinter.error("Error saving article");
                return null;
            }

            Optional<Article> optArticle = articleDao.findById(articleId);
            if(optArticle.isEmpty()) {
                timePrinter.error("Error saving article");
                return null;
            }

            ArticleView articleView = new ArticleView(optArticle.get());

            timePrinter.log();

            return articleView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean updateArticle(UserToken userToken, String id, ArticleView articleDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [UPDATE] id article" + id + ", data:" + gson.toJson(articleDetails)
        );

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Article> optArticle = articleDao.findById(objectId);

            if(optArticle.isPresent()) {
                Article article = optArticle.get();
                article.update(articleDetails);
                boolean updated = articleDao.update(article);

                timePrinter.log();

                return updated;
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public boolean deleteArticle(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [DELETE] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            boolean deleted = articleDao.delete(objectId);

            try {
                String articleKey = formArticleKey(id);
                RedisInstance.getInstance().delete(articleKey);
            } catch (Exception e) {
                // e.printStackTrace();
                MineLog.error(e.getMessage());
            }

            // TODO: create and call method where I remove all article Id in tagArticle set

            timePrinter.log();

            return deleted;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }


    public List<String> listAllTags(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [LIST] [TAGS] ");

        List<String> tags = articleDao.findAllDistinctTags();

        timePrinter.log();

        return tags;
    }

    public List<String> listIdsByTags(UserToken userToken, List<String> tags) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [LIST] [TAGS] tags: " + gson.toJson(tags)
        );

        List<String> ids = articleDao.findIdsByTags(tags);

        timePrinter.log();

        return ids;
    }

    public List<String> listIdsByTags(UserToken userToken, List<String> tags, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [LIST] [TAGS] tags: " + gson.toJson(tags)
                        + ", page: " + page
                        + ", pageSize: " + pageSize
        );

        List<String> ids = articleDao.findIdsByTags(tags, page, pageSize);

        timePrinter.log();

        return ids;
    }

    public List<String> listAllIds(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [LIST] [IDS] ");

        List<String> ids = articleDao.findAllIds();

        timePrinter.log();

        return ids;
    }

    public List<String> listAllIds(UserToken userToken, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [LIST] [IDS] page: " + page + ", size: " + pageSize
        );

        List<String> ids = articleDao.findAllIds(page, pageSize);

        timePrinter.log();

        return ids;
    }

    public List<String> listAllIdsByMe(UserToken userToken, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [LIST] [IDS] page: " + page + ", size: " + pageSize
        );

        List<String> ids = articleDao.findAllIds(page, pageSize);

        timePrinter.log();

        return ids;
    }


    public static final String SERVER_EVENT_LOG_IMPORT_ARTICLE = "article import";

    private Set<String> getArticleIdsByTagFromCache(UserToken userToken, String tag) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [CACHE] [GET ARTICLE IDS BY TAG] tag: " + tag
        );

        String key = formArticleByTagKey(tag);

        Set<String> ids = RedisInstance.getInstance().smembers(key, String.class);

        timePrinter.log();

        return ids;
    }

    private void updateCacheTagsArticles(UserToken userToken, @NotNull Article article) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [UPDATE CACHE TAG-ARTICLE] ");

        if(article.getTags() != null) {
            String articleId = StringIdConverter.getInstance().fromObjectId(article.getArticleId());
            for(String tag : article.getTags()) {
                String key = formArticleByTagKey(tag);
                RedisInstance.getInstance().sadd(key, articleId);
            }
        }


        timePrinter.log();
    }


    public long loadArticles(UserToken userToken, String source, InputStream fileStream) {
        try {
            List<Article> articles = ArticleReaderService.readArticle(userToken, source, fileStream);

            return loadArticles(userToken, articles);
        } catch(Exception e) {
            ServerEventLogService.getInstance()
                    .saveServerEventLog(
                            SERVER_EVENT_LOG_IMPORT_ARTICLE,
                            e.getClass().getSimpleName(),
                            e.getMessage(),
                            null,
                            null,
                            null,
                            null
                    );
        }

        return 0;
    }

    public long loadArticles(UserToken userToken, List<Article> articles) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [LOAD] ");

        try {
            if(!articles.isEmpty()) {
                Article articleFirst = articles.getFirst();
                System.out.println(gson.toJson(articleFirst));

            }

            for(Article article: articles) {
                ObjectId articleId = articleDao.save(article);
                if(articleId != null) {
                    article.setArticleId(articleId);
                    try {
                        updateCacheTagsArticles(userToken, article);
                    } catch (Exception ignored) { }
                }
            }

            timePrinter.log();

            ServerEventLogService.getInstance()
                    .saveServerEventLog(
                            SERVER_EVENT_LOG_IMPORT_ARTICLE,
                            "success",
                            "Number of imported articles: " + articles.size(),
                            null,
                            null,
                            null,
                            userToken.getIdUser()
                    );

            return articles.size();
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
            // e.printStackTrace();
            ServerEventLogService.getInstance()
                    .saveServerEventLog(
                            SERVER_EVENT_LOG_IMPORT_ARTICLE,
                            e.getClass().getSimpleName(),
                            e.getMessage(),
                            null,
                            null,
                            null,
                            null
                    );
        }

        return 0;
    }




    // Comments



    private void cacheComments(
            UserToken userToken,
            String articleId,
            int depth,
            int page,
            int size,
            CommentResponse response
    ) throws Exception {
        String cacheKey = buildCommentsCacheKey(userToken, articleId, depth, page, size);
        RedisInstance.getInstance().set(cacheKey, response, CACHE_TTL);
    }


    private CommentResponse getCachedComments(UserToken userToken, String articleId, int depth, int page, int size) throws Exception {
        String cacheKey = buildCommentsCacheKey(userToken, articleId, depth, page, size);
        return RedisInstance.getInstance().get(cacheKey, CommentResponse.class);
    }





    private int getCommentDepth(UserToken userToken, String articleId, String commentId) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [COMMENT DEPTH] "
                        + "article: " + articleId
                        + ", commentId: " + commentId
        );

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if(optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            return -1;
        }

        Article article = optArticle.get();

        int depth = findCommentDepth(userToken, article.getComments(), commentId, 0);

        timePrinter.log();

        return depth;
    }

    private @Nullable String findRootCommentId(UserToken userToken, String articleId, String commentId) {
        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if(optArticle.isEmpty()) return null;

        Article article = optArticle.get();

        return commentService.findRootCommentIdRecursive(userToken, article.getComments(), commentId);
    }

    private int findCommentDepth(UserToken userToken, @NotNull List<EmbeddedComment> comments, String targetCommentId, int currentDepth) {
        for (EmbeddedComment comment : comments) {
            if (Objects.equals(
                    StringIdConverter.getInstance()
                            .fromObjectId(comment.getCommentId()),
                    targetCommentId)
            ) {
                return currentDepth;
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                int foundDepth = findCommentDepth(userToken, comment.getReplies(), targetCommentId, currentDepth + 1);
                if (foundDepth != -1) {
                    return foundDepth;
                }
            }
        }
        return -1;
    }

    private @Nullable String findCommentPath(UserToken userToken, String articleId, String commentId) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [COMMENT PATH] "
                        + "articleId: " + articleId
                        + ", commentId: " + commentId
        );

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if(optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            return null;
        }

        Article article = optArticle.get();

        String path = findCommentPathRecursive(userToken, article.getComments(), commentId, "comments");

        timePrinter.log();

        return path;
    }

    private @Nullable String findCommentPathRecursive(
            UserToken userToken,
            @NotNull List<EmbeddedComment> comments,
            String targetId,
            String currentPath
    ) {
        for (int i = 0; i < comments.size(); i++) {
            EmbeddedComment comment = comments.get(i);
            String path = currentPath + "." + i;

            if (Objects.equals(
                    StringIdConverter.getInstance()
                            .fromObjectId(comment.getCommentId()),
                    targetId)
            ) {
                return path;
            }

            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                String foundPath = findCommentPathRecursive(
                        userToken,
                        comment.getReplies(),
                        targetId,
                        path + ".replies");
                if (foundPath != null) {
                    return foundPath;
                }
            }
        }
        return null;
    }

    private @Nullable String findCommentPath(UserToken userToken, String articleId, String commentId, int depth) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [COMMENT PATH] "
                        + "articleId: " + articleId
                        + ", commentId: " + commentId
                        + ", depth: " + depth
        );

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if(optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            return null;
        }

        Article article = optArticle.get();

        String path =  findCommentPathRecursive(userToken, article.getComments(), commentId, depth, "comments");

        timePrinter.log();

        return path;
    }

    private @Nullable String findCommentPathRecursive(
            UserToken userToken,
            @NotNull List<EmbeddedComment> comments,
            String targetId,
            int depth,
            String currentPath
    ) {
        for (int i = 0; i < comments.size(); i++) {
            EmbeddedComment comment = comments.get(i);

            if (Objects.equals(
                    StringIdConverter.getInstance()
                            .fromObjectId(comment.getCommentId()),
                    targetId)
            ) {
                return currentPath + "." + i;
            }

            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                String foundPath = findCommentPathRecursive(
                        userToken,
                        comment.getReplies(),
                        targetId,
                        depth,
                        currentPath + "." + i + ".replies"
                );

                if (foundPath != null) {
                    return foundPath;
                }
            }
        }
        return null;
    }



    private @NotNull CommentResponse buildCommentResponse(
            UserToken userToken,
            Article article,
            int maxDepth,
            int page,
            int size
    ) {
        CommentResponse response = new CommentResponse(article);

        // Paginate top-level comments
        List<EmbeddedComment> paginatedComments = PaginateUtils.paginateList(article.getComments(), page, size);

        List<Comment> comments = paginatedComments.stream()
                .map(comment -> commentService.buildCommentTree(userToken, comment, maxDepth))
                .collect(Collectors.toList());

        response.setComments(comments);
        response.setPage(page);
        response.setSize(size);
        response.setHasMore((page + 1) * size < article.getTopLevelCommentsCount());

        return response;
    }





    /**
     * Add a comment (top-level or reply)
     */
    public Comment addComment(UserToken userToken, String articleId, @NotNull CommentRequest commentRequest) throws Exception {
        if (commentRequest.getParentCommentId() == null) {
            return addTopLevelComment(userToken, articleId, commentRequest);
        } else {
            return addReplyComment(userToken, articleId, commentRequest);
        }
    }

    /**
     * Add a top-level comment
     */
    @Contract("_, _, _ -> new")
    private @NotNull Comment addTopLevelComment(@NotNull UserToken userToken, String articleId, CommentRequest commentRequest) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [UPDATE] [ADD TOP LEVEL COMMENT] "
                        + "articleId: " + articleId
                        + " , commentRequest: " + gson.toJson(commentRequest)
        );

        // TODO: add check number of comment in article and add in deepReplies if more than ...

        EmbeddedComment newComment = new EmbeddedComment(articleId, commentRequest, 0);

        articleDao.addTopLeveComment(articleId, newComment, userToken.getIdUser());

        timePrinter.log();

        // Invalidate cache
        invalidateCommentCache(userToken, articleId);

        return new Comment(newComment);
    }

    private void updateParentHasMoreReplies(UserToken userToken, String articleId, String parentCommentId) {
        int parentDepth = getCommentDepth(userToken, articleId, parentCommentId);

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [UPDATE] [PARENT HAS MORE REPLIES] "
                        + "article: " + articleId
                        + ", parent comment: " + parentCommentId
                        + ", parent depth: " + parentDepth
        );

        boolean updated;
        if (parentDepth < MAX_EMBEDDED_DEPTH) {
            // Update embedded comment
            final String path = findCommentPath(userToken, articleId, parentCommentId);

            if(path == null || path.isBlank()) {
                updated = false;
            } else {
                updated = articleDao.updateHasMoreReplies(articleId, path, true, userToken.getIdUser());
            }
        } else {
            // Update deep comment - this would require additional logic
            // since deep comments are stored separately
            updated = commentService.updateParentHasMoreReplies(userToken, articleId, parentCommentId);
        }

        if(updated) {
            timePrinter.log();
        } else {
            timePrinter.missing("Parent comment not found or not modified");
        }
    }

    /**
     * Add a reply comment (embedded or deep)
     */
    private Comment addReplyComment(
            UserToken userToken,
            String articleId,
            @NotNull CommentRequest commentRequest
    ) throws Exception {
        String parentId = commentRequest.getParentCommentId();
        int parentDepth = getCommentDepth(userToken, articleId, parentId);
        int newDepth = parentDepth + 1;

        if (newDepth <= MAX_EMBEDDED_DEPTH) {
            return addEmbeddedReply(userToken, articleId, commentRequest, parentId, newDepth);


        } else {
            String rootCommentId = findRootCommentId(userToken, articleId, parentId);
            Comment comment = commentService.addDeepReply(userToken, articleId, commentRequest, parentId, rootCommentId, newDepth);

            if(comment != null) {
                // Update the parent comment to indicate it has more replies
                updateParentHasMoreReplies(userToken, articleId, parentId);

                // Update article comment count
                articleDao.incCommentsCount(articleId, 1, userToken.getIdUser());

                // Invalidate cache
                invalidateCommentCache(userToken, articleId);
            }

            return comment;
        }
    }

    /**
     * Add an embedded reply (depth <= MAX_EMBEDDED_DEPTH)
     */
    private @Nullable Comment addEmbeddedReply(
            UserToken userToken,
            String articleId,
            CommentRequest commentRequest,
            String parentId,
            int depth
    ) throws Exception {
        EmbeddedComment newReply = new EmbeddedComment(articleId, commentRequest, depth);

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [UPDATE] [ADD EMBEDDED REPLY] "
                        + "articleId: " + articleId
                        + ", parentId: " + parentId
                        + ", depth: " + depth
                        + " , commentRequest: " + gson.toJson(newReply)
        );

        String arrayPath = findCommentPath(userToken, articleId, parentId);
        if (arrayPath == null) {
            throw new RessourceNotFound("Parent comment not found");
        }

        boolean updated = articleDao.addEmbeddedReply(articleId, newReply, arrayPath, userToken.getIdUser());

        if(updated) {
            timePrinter.log();

            // Invalidate cache
            invalidateCommentCache(userToken, articleId);

            return new Comment(newReply);
        }

        timePrinter.error();
        return null;
    }


    public CommentResponse getComments(
            UserToken userToken,
            String articleId,
            int maxDepth,
            int page,
            int size
    ) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [COMMENTS] "
                        + "articleId: " + articleId
                        + ", maxDepth: " + maxDepth
                        + ", page: " + page
                        + ", size: " + size
        );

        // Try to get from cache first
        CommentResponse cachedResponse = getCachedComments(userToken, articleId, maxDepth, page, size);
        if (cachedResponse != null) {
            timePrinter.log();
            return cachedResponse;
        }

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if (optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            throw new RessourceNotFound("Article not found");
        }

        Article article = optArticle.get();

        CommentResponse response = buildCommentResponse(userToken, article, maxDepth, page, size);

        // Cache the response
        cacheComments(userToken, articleId, maxDepth, page, size, response);

        timePrinter.log();

        return response;
    }

    public ReplyResponse getReplies(
            UserToken userToken,
            String articleId,
            String commentId,
            int page,
            int size
    ) throws Exception {
        int commentDepth = getCommentDepth(userToken, articleId, commentId);

        if (commentDepth < MAX_EMBEDDED_DEPTH) {
            // Get embedded replies
            return getEmbeddedReplies(userToken, articleId, commentId, page, size);
        } else {
            // Get deep replies
            return commentService.getDeepReplies(userToken, articleId, commentId, page, size);
        }
    }


    private @NotNull ReplyResponse getEmbeddedReplies(
            UserToken userToken,
            String articleId,
            String commentId,
            int page,
            int size
    ) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [EMBEDDED REPLIES] "
                        + "articleId: " + articleId
                        + ", commentId: " + commentId
                        + ", page: " + page
                        + ", size: " + size
        );

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if (optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            throw new RessourceNotFound("Article not found");
        }

        Article article = optArticle.get();

        EmbeddedComment parentComment = commentService.findCommentById(userToken, article.getComments(), commentId);
        if (parentComment == null) {
            timePrinter.missing("Comment not found");
            throw new RuntimeException("Comment not found");
        }

        List<EmbeddedComment> allReplies = parentComment.getReplies();
        List<EmbeddedComment> paginatedReplies = PaginateUtils.paginateList(allReplies, page, size);

        ReplyResponse response = new ReplyResponse();
        response.setReplies(paginatedReplies.stream()
                .map(Comment::new)
                .collect(Collectors.toList()));
        response.setTotalReplies(allReplies.size());
        response.setPage(page);
        response.setSize(size);
        response.setHasMore((page + 1) * size < allReplies.size());

        timePrinter.log();

        return response;
    }



    public void deleteComment(UserToken userToken, String articleId, String commentId) throws Exception {
        int depth = getCommentDepth(userToken, articleId, commentId);

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [DELETE] [COMMENT] "
                        + "articleId: " + articleId
                        + ", commentId: " + commentId
                        + ", depth: " + depth
        );

        if (depth <= MAX_EMBEDDED_DEPTH) {
            deleteEmbeddedComment(userToken, articleId, commentId);
        } else {
            int inc = commentService.deleteDeepComment(userToken, articleId, commentId);
            if(inc != 0) articleDao.incCommentsCount(articleId, inc, userToken.getIdUser());
        }

        timePrinter.log();

        // Invalidate cache
        invalidateCommentCache(userToken, articleId);
    }

    private void deleteEmbeddedComment(UserToken userToken, String articleId, String commentId) {
        String path = findCommentPath(userToken, articleId, commentId);
        ((ArticleRepository) articleDao).decTopLevelCommentsCount(articleId, path, userToken.getIdUser());
    }




    // Reactions


    private void cacheReactions(
            UserToken userToken,
            String articleId,
            int page,
            int size,
            UserReactionsResponse response
    ) throws Exception {
        String cacheKey = formReactionKey(userToken, articleId, page, size);
        RedisInstance.getInstance().set(cacheKey, response, CACHE_TTL);
    }

    private UserReactionsResponse getCachedReactions(UserToken userToken, String articleId, int page, int size) throws Exception {
        String cacheKey = formReactionKey(userToken, articleId, page, size);
        return RedisInstance.getInstance().get(cacheKey, UserReactionsResponse.class);
    }


    private UserReactionsResponse buildReactionResponse(
            UserToken userToken,
            Article article,
            int page,
            int size
    ) {
        UserReactionsResponse response = new UserReactionsResponse(article);

        List<EmbeddedReaction> paginatedReactions = PaginateUtils.paginateList(article.getReactions(), page, size);

        List<UserReaction> reactions = paginatedReactions.stream().map(UserReaction::new).toList();

        response.setReactions(reactions);
        response.setPage(page);
        response.setSize(size);
        response.setHasMore((page + 1) * size < reactions.size());

        return response;
    }


    public UserReactionsResponse getReactions(
            UserToken userToken,
            String articleId,
            int page,
            int size
    ) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [GET] [COMMENTS] "
                        + "articleId: " + articleId
                        + ", page: " + page
                        + ", size: " + size
        );

        UserReactionsResponse cachedResponse = getCachedReactions(userToken, articleId, page, size);
        if(cachedResponse != null) {
            timePrinter.log();
            return cachedResponse;
        }

        ObjectId objectId = new ObjectId(articleId);
        Optional<Article> optArticle = articleDao.findById(objectId);

        if (optArticle.isEmpty()) {
            timePrinter.missing("Article not found");
            throw new RessourceNotFound("Article not found");
        }

        Article article = optArticle.get();

        UserReactionsResponse response = buildReactionResponse(userToken, article, page, size);

        cacheReactions(userToken, articleId, page, size, response);

        timePrinter.log();

        return response;
    }


    public UserReaction addReaction(UserToken userToken, String articleId, ReactionRequest reactionRequest) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [UPDATE] [ADD EMBEDDED REPLY] "
                        + "articleId: " + articleId
                        + " , reactionRequest: " + gson.toJson(reactionRequest)
        );

        // TODO: add case choice between add in embedded or in user reactions

        UserReaction reaction = addEmbeddedReaction(userToken, articleId, reactionRequest);

        if(reaction != null) {
            timePrinter.log();

            // Invalide cache
            invalidateReactionCache(userToken, articleId);

            return reaction;
        }

        timePrinter.error();

        return null;
    }


    private UserReaction addEmbeddedReaction(UserToken userToken, String articleId, ReactionRequest reactionRequest) throws Exception {
        EmbeddedReaction newReaction = new EmbeddedReaction(articleId, reactionRequest);

        boolean updated = articleDao.addEmbeddedReaction(articleId, newReaction, userToken.getIdUser());

        if(updated) {
            return new UserReaction(newReaction);
        }

        return null;
    }


    private UserReaction addUserReaction(UserToken userToken, String articleId, ReactionRequest reactionRequest) throws Exception {

        return reactionService.toggleReaction(userToken, EntityTypeEnum.Article, articleId, reactionRequest);

    }



}
