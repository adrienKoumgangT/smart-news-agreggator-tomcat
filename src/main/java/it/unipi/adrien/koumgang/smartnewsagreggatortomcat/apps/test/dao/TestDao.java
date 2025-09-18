package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.Test;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface TestDao {

    Optional<Test> findById(ObjectId id);
    List<Test> findAll();
    List<String> findAllIds();
    List<Test> findAll(int page, int pageSize);
    List<String> findAllIds(int page, int pageSize);
    ObjectId save(Test test);
    boolean update(Test test);
    boolean delete(ObjectId id);
    long count();

}