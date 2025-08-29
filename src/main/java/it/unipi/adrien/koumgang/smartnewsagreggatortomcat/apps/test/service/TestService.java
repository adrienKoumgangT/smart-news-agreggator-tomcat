package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.dao.TestDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.Test;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.repository.TestRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.CacheManager;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public class TestService {

    private final TestDao testDao;

    public TestService(TestDao dao) { this.testDao = dao; }


    public static TestService getInstance() {
        return new TestService(TestRepository.getInstance());
    }

    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    private static String formTestKey(String id) {
        return "test:" + id;
    }

    public Optional<TestView> getTestById(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return Optional.empty();
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [GET] id: " + id);

        String testKey = formTestKey(id);

        try {
            Object cacheData = CacheManager.getInstance().getHandler().get(testKey);
            if(cacheData instanceof Test) {
                TestView testView = new TestView((Test) cacheData);

                return Optional.of(testView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Test> optTest = testDao.findById(objectId);

            if(optTest.isPresent()) {
                try {
                    CacheManager.getInstance().getHandler().set(testKey, optTest.get(), 60*10);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TestView testView = new TestView(optTest.get());

                timePrinter.log();

                return Optional.of(testView);
            } else {
                timePrinter.error("Test not found");
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return Optional.empty();
    }

    public TestView saveTest(TestView testDetails) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [SAVE] test: " + gson.toJson(testDetails));

        try {
            Test test = new Test(testDetails);
            Test testNew = testDao.save(test);

            TestView testView = new TestView(testNew);

            timePrinter.log();

            return testView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean updateTest(String id, TestView testDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [UPDATE] test: " + gson.toJson(testDetails));

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Test> existingTest = testDao.findById(objectId);

            if (existingTest.isPresent()) {
                Test test = existingTest.get();
                // test.setName(testDetails.getName());
                test.setDescription(testDetails.getDescription());
                test.setActive(testDetails.getActive());
                boolean updated = testDao.update(test);

                timePrinter.log();

                return updated;
            }
            return false;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return false;
        }
    }

    public boolean deleteTest(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [DELETE] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            boolean deleted = testDao.delete(objectId);

            try {
                String testKey = formTestKey(id);
                CacheManager.getInstance().getHandler().delete(testKey);
            } catch (Exception e) {
                e.printStackTrace();
            }

            timePrinter.log();

            return deleted;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public List<TestView> listTests() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [LIST] ");

        List<Test> tests = testDao.findAll();

        List<TestView> testViews = tests.stream().map(TestView::new).toList();

        timePrinter.log();

        return testViews;
    }

    public long count() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [COUNT] ");

        long count = testDao.count();

        timePrinter.log();

        return count;
    }
}