package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.DeepComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.DeepReplies;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface DeepRepliesDao {

    Optional<DeepReplies> findById(ObjectId id);

    long count();
    List<DeepReplies> findAll();
    List<DeepReplies> findAll(int page, int pageSize);

    long count(String entityId);
    List<DeepReplies> findByEntityId(String entityId);
    List<DeepReplies> findByEntityId(String entityId, int page, int pageSize);

    long count(String entityId, String parentId);
    long count(String entityId, String parentId, Integer depth);
    List<DeepReplies> findByParentId(String entityId, String parentId);
    List<DeepReplies> findByParentId(String entityId, String parentId, Integer depth);
    List<DeepReplies> findByParentId(String entityId, String parentId, Integer depth, int page, int pageSize);

    List<DeepComment> findDeepCommentByParentId(String entityId, String parentId);
    List<DeepComment> findDeepCommentByParentId(String entityId, String parentId, Integer depth);

    ObjectId save(DeepReplies deepReplies);
    boolean update(DeepReplies deepReplies);
    boolean delete(ObjectId id);


    boolean updateHasMoreReplies(String entityId, String parentId, boolean hasMore, String metaUser);
}
