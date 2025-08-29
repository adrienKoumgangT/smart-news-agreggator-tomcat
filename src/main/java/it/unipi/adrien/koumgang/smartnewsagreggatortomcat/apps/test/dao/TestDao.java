package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.Test;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface TestDao {

    Optional<Test> findById(ObjectId id);
    List<Test> findAll();
    Test save(Test test);
    boolean update(Test test);
    boolean delete(ObjectId id);
    long count();

}