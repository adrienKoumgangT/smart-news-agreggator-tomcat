package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.dao.TestDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.Test;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.repository.TestRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.RedisInstance;
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

    public TestView getTestById(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [GET] id: " + id);

        String testKey = formTestKey(id);

        try {
            TestView cacheData = RedisInstance.getInstance().get(testKey, TestView.class);
            if(cacheData != null) {
                timePrinter.log();
                return cacheData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Test> optTest = testDao.findById(objectId);

            if(optTest.isPresent()) {
                try {
                    RedisInstance.getInstance().set(testKey, optTest.get(), 60*10);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TestView testView = new TestView(optTest.get());

                timePrinter.log();

                return testView;
            } else {
                timePrinter.missing("Test not found");
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public TestView saveTest(TestView testDetails) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [SAVE] test: " + gson.toJson(testDetails));

        try {
            Test test = new Test(testDetails);
            ObjectId testId = testDao.save(test);

            if(testId == null) {
                timePrinter.error("Error saving test");
                return null;
            }

            Optional<Test> optTest = testDao.findById(testId);

            if(optTest.isEmpty()) {
                timePrinter.error("Error saving test");
                return null;
            }

            TestView testView = new TestView(optTest.get());

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
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
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
                RedisInstance.getInstance().delete(testKey);
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

    public List<String> listIds() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [LIST] [IDS] ");

        List<String> ids = testDao.findAllIds();

        timePrinter.log();

        return ids;
    }

    public List<TestView> listTests(int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [LIST] page: " + page + ", size: " + pageSize);

        List<Test> tests = testDao.findAll(page, pageSize);

        List<TestView> testViews = tests.stream().map(TestView::new).toList();

        timePrinter.log();

        return testViews;
    }

    public List<String> listIds(int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [LIST] [IDS] page: " + page + ", size: " + pageSize);

        List<String> ids = testDao.findAllIds(page, pageSize);

        timePrinter.log();

        return ids;
    }

    public long count() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [TEST] [COUNT] ");

        long count = testDao.count();

        timePrinter.log();

        return count;
    }
}