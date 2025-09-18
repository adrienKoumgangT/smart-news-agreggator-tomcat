package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.EmbeddedComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionEnum;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface ArticleDao {

    Optional<Article> findById(ObjectId id);

    List<String> findAllDistinctTags();

    List<String> findIdsByTag(String tag);
    List<String> findIdsByTags(List<String> tags);
    List<String> findIdsByTags(List<String> tags, int page, int pageSize);

    List<Article> findAll();
    List<String> findAllIds();
    List<Article> findAll(int page, int pageSize);
    List<String> findAllIds(int page, int pageSize);
    long count();

    ObjectId save(Article article);
    boolean update(Article article);
    boolean delete(ObjectId id);


    boolean addTopLeveComment(final String articleId, EmbeddedComment newComment, final String metaUser);
    boolean addEmbeddedReply(final String articleId, EmbeddedComment newReply, final String arrayPath, final String metaUser);
    boolean incTopLevelCommentsCount(final String articleId, final String path, final int inc, final String metaUser);
    boolean updateHasMoreReplies(final String articleId, final String path, final boolean hasMoreReplies, final String metaUser);
    boolean incCommentsCount(final String articleId, final int inc, final String metaUser);


    boolean addEmbeddedReaction(final String articleId, final EmbeddedReaction newReaction, final String metaUser);
    boolean incReactionsCount(final String articleId, final ReactionEnum reactionEnum, final int inc, final String metaUser);

}
