package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.dao.DeepRepliesDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.repository.DeepRepliesRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.service.ReactionService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service.BaseService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils.PaginateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommentService extends BaseService {


    public static CommentService getInstance() {
        return new CommentService(DeepRepliesRepository.getInstance());
    }



    private final DeepRepliesDao deepRepliesDao;

    private final ReactionService reactionService = ReactionService.getInstance();

    public CommentService(DeepRepliesDao deepRepliesDao) {
        this.deepRepliesDao     = deepRepliesDao;
    }


    private static final int MAX_EMBEDDED_DEPTH = 2;



    public Comment addDeepReply(
            UserToken userToken,
            String entityId,
            CommentRequest commentRequest,
            String parentId,
            String rootCommentId,
            int depth
    ) throws Exception {
        DeepComment newReply = new DeepComment(commentRequest, depth);

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [COMMENT] [UPDATE] [ADD DEEP REPLY] "
                        + "entityId: " + entityId
                        + ", parentId: " + parentId
                        + ", depth: " + depth
                        + " , commentRequest: " + gson.toJson(newReply)
        );

        // Find or create deep replies document
        DeepReplies deepReplies;

        List<DeepReplies> deepRepliesList = deepRepliesDao.findByParentId(entityId, parentId);
        if (deepRepliesList.isEmpty()) {
            deepReplies = createNewDeepReplies(userToken, entityId, parentId, rootCommentId, depth);
        } else {
            deepReplies = deepRepliesList.getLast();
        }

        deepReplies.getReplies().add(newReply);
        deepReplies.setReplyCount(deepReplies.getReplyCount() + 1);
        deepReplies.setUpdatedAt(new Date());

        boolean updated = deepRepliesDao.update(deepReplies);

        if(updated) {
            timePrinter.log();

            // Update the parent comment to indicate it has more replies
            updateParentHasMoreReplies(userToken, entityId, parentId);



            return new Comment(newReply);
        }

        timePrinter.error();

        return null;
    }


    private DeepReplies createNewDeepReplies(
            UserToken userToken,
            String entityId,
            String parentReplyId,
            String rootCommentId,
            int depth
    ) {
        DeepReplies deepReplies = new DeepReplies(entityId, parentReplyId, depth);

        // Set root comment ID by finding the top-level parent
        deepReplies.setRootCommentId(rootCommentId);

        return deepReplies;
    }

    public @Nullable EmbeddedComment findCommentById(UserToken userToken, @NotNull List<EmbeddedComment> comments, String commentId) {
        for (EmbeddedComment comment : comments) {
            if (Objects.equals(StringIdConverter.getInstance().fromObjectId(comment.getCommentId()), commentId)) {
                return comment;
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                EmbeddedComment found = findCommentById(userToken, comment.getReplies(), commentId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }


    public String findRootCommentIdRecursive(
            UserToken userToken,
            @NotNull List<EmbeddedComment> comments,
            String targetId
    ) {
        for (EmbeddedComment comment : comments) {
            if (Objects.equals(
                    StringIdConverter.getInstance()
                            .fromObjectId(comment.getCommentId()),
                    targetId)
            ) {
                return StringIdConverter.getInstance().fromObjectId(comment.getCommentId()); // This is the root comment
            }
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                String rootId = findRootCommentIdRecursive(userToken, comment.getReplies(), targetId);
                if (rootId != null) {
                    // If we're at depth 1, the current comment is the root
                    if (comment.getDepth() == 0) {
                        return StringIdConverter.getInstance().fromObjectId(comment.getCommentId());
                    }
                    return rootId;
                }
            }
        }
        return null;
    }


    public ReplyResponse getDeepReplies(
            UserToken userToken,
            String entityId,
            String commentId,
            int page,
            int size
    ) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [COMMENT] [GET] [DEEP REPLIES] "
                        + "entityId: " + entityId
                        + ", commentId: " + commentId
                        + ", page: " + page
                        + ", size: " + size
        );

        List<DeepReplies> deepRepliesOpt = deepRepliesDao
                .findByParentId(entityId, commentId);

        if (deepRepliesOpt.isEmpty()) {
            timePrinter.log();
            return new ReplyResponse(Collections.emptyList(), 0, page, size, false);
        }

        DeepReplies deepReplies = deepRepliesOpt.getFirst();
        List<DeepComment> allReplies = deepReplies.getReplies();
        List<DeepComment> paginatedReplies = PaginateUtils.paginateList(allReplies, page, size);

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



    public boolean updateParentHasMoreReplies(UserToken userToken, String entityId, String parentCommentId) {

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [COMMENT] [UPDATE] [PARENT HAS MORE REPLIES] "
                        + "entity: " + entityId
                        + ", parent comment: " + parentCommentId
        );

        boolean updated = deepRepliesDao.updateHasMoreReplies(
                entityId,
                parentCommentId,
                true,
                userToken.getIdUser()
        );

        if(updated) {
            timePrinter.log();
        } else {
            timePrinter.missing("Parent comment not found or not modified");
        }

        return updated;
    }



    public Comment buildCommentTree(UserToken userToken, EmbeddedComment embeddedComment, int maxDepth) {
        Comment comment = new Comment(embeddedComment);

        if (embeddedComment.getDepth() < maxDepth - 1 && embeddedComment.getReplies() != null) {
            List<Comment> replies = embeddedComment.getReplies().stream()
                    .map(reply -> buildCommentTree(userToken, reply, maxDepth))
                    .collect(Collectors.toList());
            comment.setReplies(replies);
        }

        // Load deep replies if needed
        if (
                Objects.equals(embeddedComment.getHasMoreReplies(), true)
                        && (embeddedComment.getDepth() == null || embeddedComment.getDepth() < maxDepth - 1)
        ) {
            List<DeepReplies> deepRepliesList = deepRepliesDao
                    .findByParentId(
                            embeddedComment.getEntityId(),
                            StringIdConverter.getInstance()
                                    .fromObjectId(embeddedComment.getCommentId())
                    );

            List<Comment> additionalReplies = deepRepliesList.stream()
                    .flatMap(dr -> dr.getReplies().stream())
                    .map(Comment::new)
                    .collect(Collectors.toList());

            if (comment.getReplies() == null) {
                comment.setReplies(additionalReplies);
            } else {
                comment.getReplies().addAll(additionalReplies);
            }
        }

        return comment;
    }



    public int deleteDeepComment(UserToken userToken, String entityId, String commentId) {
        // Find and remove from deep replies
        List<DeepReplies> allDeepReplies = deepRepliesDao.findByEntityId(entityId);

        int inc = 0;
        for (DeepReplies dr : allDeepReplies) {
            boolean removed = dr.getReplies()
                    .removeIf(
                            reply -> StringIdConverter.getInstance()
                                    .fromObjectId(reply.getDeepCommentId())
                                    .equals(commentId)
                    );

            if (removed) {
                dr.setReplyCount(dr.getReplyCount() - 1);
                deepRepliesDao.save(dr);

                inc -= 1;
                break;
            }
        }

        return inc;
    }



    // Reactions



}
