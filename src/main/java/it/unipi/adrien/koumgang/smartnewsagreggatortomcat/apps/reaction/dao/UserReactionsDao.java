package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReactions;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.EntityTypeEnum;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserReactionsDao {

    Optional<UserReactions> findById(ObjectId id);

    long count();
    List<UserReactions> findAll();
    List<UserReactions> findAll(int page, int pageSize);

    long count(EntityTypeEnum entityType, String entityId);
    List<UserReactions>  findByArticleId(EntityTypeEnum entityType, String entityId);
    List<UserReactions>  findByArticleId(EntityTypeEnum entityType, String entityId, int page, int pageSize);

    Optional<EmbeddedReaction>  findByArticleId(EntityTypeEnum entityType, String entityId, String AuthorId);

    ObjectId save(UserReactions userReactions);
    boolean update(UserReactions userReactions);
    boolean delete(ObjectId id);

}
