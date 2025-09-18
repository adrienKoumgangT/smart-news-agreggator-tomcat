package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model.MetaData;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface MetaDataDao {

    Optional<MetaData> findById(ObjectId id);

    List<MetaData> findAll();
    List<MetaData> findAll(int page, int pageSize);
    long count();

    List<MetaData> findAll(String metaType);
    List<MetaData> findAll(String metaType, int page, int pageSize);
    long count(String metaType);

    Optional<MetaData> findByName(String metaType, String name);

    ObjectId save(MetaData metaData);
    boolean update(MetaData metaData);
    boolean delete(ObjectId id);

}
