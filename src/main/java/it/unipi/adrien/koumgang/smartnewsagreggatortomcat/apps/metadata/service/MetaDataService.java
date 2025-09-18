package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.service;

import com.google.gson.reflect.TypeToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.dao.MetaDataDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model.MetaData;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.repository.MetaDataRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataSimpleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.RedisInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service.BaseService;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class MetaDataService extends BaseService {

    @Contract(" -> new")
    public static @NotNull MetaDataService getInstance() {
        return new MetaDataService(MetaDataRepository.getInstance());
    }


    private final MetaDataDao metaDataDao;

    public MetaDataService(MetaDataDao metaDataDao) {
        this.metaDataDao = metaDataDao;
    }



    private static final Long TTL_CACHE_METADATA = 60 * 60L;



    private static @NotNull @Unmodifiable List<MetaDataView> getMetaDataViews(@NotNull List<MetaData> metaDataList) {
        return metaDataList.stream().map(MetaDataView::new).toList();
    }


    @Contract(pure = true)
    private static @NotNull String formMetadataKey(String id) {
        return "metadata:"  + id;
    }

    @Contract(pure = true)
    private static @NotNull String formMetadataKeyByType(String metaType) {
        return "metadata:type:"  + metaType;
    }


    @Contract(pure = true)
    private static @NotNull String formMetadataKeyByName(String metaType, String name) {
        return "metadata:type:"  + metaType + ":name:" + name;
    }

    @Nullable
    private MetaDataView getMetaDataFromCache(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [CACHE] [GET] id: " + id);

        String key = formMetadataKey(id);

        try {
            MetaDataView metaDataView = RedisInstance.getInstance().get(key, MetaDataView.class);
            if (metaDataView != null) {
                try {
                    RedisInstance.getInstance().expire(key, TTL_CACHE_METADATA);
                } catch (Exception ignored) {}

                timePrinter.log();
                return metaDataView;
            }
            timePrinter.missing();
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    private void cacheMetaData(UserToken userToken, MetaDataView metaDataView) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [CACHE] [SET] data: " + gson.toJson(metaDataView)
        );

        String key = formMetadataKey(metaDataView.getIdMetaData());

        try {
            RedisInstance.getInstance().set(key, metaDataView, TTL_CACHE_METADATA);
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }
    }

    private void scacheMetaData(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [CACHE] [DELETE] id: " + id);

        String key = formMetadataKey(id);

        try {
            RedisInstance.getInstance().delete(key);
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }
    }

    private @Nullable List<MetaDataView> getMetaDataByTypeFromCache(UserToken userToken, String metaType) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [CACHE] [GET] meta Type: " + metaType
        );

        String key = formMetadataKeyByType(metaType);

        try {
            String value = RedisInstance.getInstance().get(key, String.class);
            if(value != null) {
                Type type = new TypeToken<List<MetaDataView>>(){}.getType();
                List<MetaDataView> metaDataViews = gson.fromJson(value, type);

                try {
                    RedisInstance.getInstance().expire(key, TTL_CACHE_METADATA);
                } catch (Exception ignored) {}

                timePrinter.log();
                return metaDataViews;
            }
            timePrinter.missing();
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    private void cacheMetaDataByType(UserToken userToken, String metaType, List<MetaDataView> metaDataViews) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [CACHE] [SET] meta type:" + metaType + ", data: " + gson.toJson(metaDataViews)
        );

        String key = formMetadataKeyByType(metaType);

        try {
            String value = gson.toJson(metaDataViews);
            RedisInstance.getInstance().set(key, value, TTL_CACHE_METADATA);
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }
    }

    private void scacheMetaDataByType(UserToken userToken, String metaType) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [CACHE] [DELET] meta type: " + metaType
        );

        String key = formMetadataKeyByType(metaType);

        try {
            RedisInstance.getInstance().delete(key);
        } catch (Exception e) {
            timePrinter.error(e.getMessage());
        }
    }

    private void scacheMetaData(UserToken userToken, String id,  String metaType) {
        scacheMetaData(userToken, id);
        scacheMetaDataByType(userToken, metaType);
    }


    public MetaDataView getMetaDataById(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }


        MetaDataView cachedData = getMetaDataFromCache(userToken, id);
        if(cachedData != null) return cachedData;


        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [GET] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<MetaData> optMetaData = metaDataDao.findById(objectId);

            if(optMetaData.isEmpty()) {
                timePrinter.missing("MetaData not found");
                return null;
            }

            MetaDataView metaDataView = new MetaDataView(optMetaData.get());

            timePrinter.log();

            return metaDataView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return null;
        }
    }


    public List<MetaDataView> listMetaData(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [LIST] ");

        List<MetaData> metaDataList = metaDataDao.findAll();

        List<MetaDataView> metaDataViews = getMetaDataViews(metaDataList);

        timePrinter.log();

        return metaDataViews;
    }

    public List<MetaDataView> listMetaData(UserToken userToken, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [LIST] page: " + page + ", size: " + pageSize
        );

        List<MetaData> metaDataList = metaDataDao.findAll(page, pageSize);

        List<MetaDataView> metaDataViews = getMetaDataViews(metaDataList);

        timePrinter.log();

        return metaDataViews;
    }

    public long count(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [COUNT] ");

        long count = metaDataDao.count();

        timePrinter.log();

        return count;
    }


    public List<MetaDataView> listMetaDataByType(UserToken userToken, String metaType) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [LIST] meta type: " + metaType
        );

        List<MetaData> metaDataList = metaDataDao.findAll(metaType);

        List<MetaDataView> metaDataViews = getMetaDataViews(metaDataList);

        timePrinter.log();

        return metaDataViews;
    }

    public List<MetaDataView> listMetaDataByType(UserToken userToken, String metaType, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [LIST] meta type: " + metaType + ", page: " + page + ", size: " + pageSize
        );

        List<MetaData> metaDataList = metaDataDao.findAll(metaType, page, pageSize);

        List<MetaDataView> metaDataViews = getMetaDataViews(metaDataList);

        timePrinter.log();

        return metaDataViews;
    }

    public long count(UserToken userToken, String metaType) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [COUNT] meta type: " + metaType
        );

        long count = metaDataDao.count(metaType);

        timePrinter.log();

        return count;
    }

    public MetaDataView listMetaDataByName(UserToken userToken, String metaType, String name) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [LIST] meta type: " + metaType + ", name: " + name
        );

        Optional<MetaData> optMetaData = metaDataDao.findByName(metaType, name);

        if(optMetaData.isEmpty()) {
            timePrinter.missing("MetaData not found");
            return null;
        }

        MetaDataView metaDataView = new MetaDataView(optMetaData.get());

        timePrinter.log();

        return metaDataView;
    }

    public MetaDataView saveMetaData(UserToken userToken, MetaDataSimpleView metaDataSimpleView, String metaType) {
        MetaDataView metaDataView = new MetaDataView(metaDataSimpleView, metaType);

        return saveMetaData(userToken, metaDataView);
    }

    public MetaDataView saveMetaData(UserToken userToken, MetaDataView metaDataDetails) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [SAVE] " + gson.toJson(metaDataDetails)
        );

        try {
            MetaData metaData = new MetaData(metaDataDetails);
            ObjectId metaDataId = metaDataDao.save(metaData);

            if(metaDataId == null) {
                timePrinter.error("Error saving metadata");
                return null;
            }

            Optional<MetaData> optMetaData = metaDataDao.findById(metaDataId);

            if(optMetaData.isEmpty()) {
                timePrinter.missing("Error saving metadata");
                return null;
            }

            MetaDataView metaDataView = new MetaDataView(optMetaData.get());

            cacheMetaData(userToken, metaDataView);

            timePrinter.log();

            return metaDataView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean updateMetadata(UserToken userToken, String id, MetaDataView metaDataDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [METADATA] [UPDATE] id metadata" + id + ", data:" + gson.toJson(metaDataDetails)
        );

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<MetaData> optMetaData = metaDataDao.findById(objectId);

            if(optMetaData.isPresent()) {
                MetaData metaData = optMetaData.get();
                metaData.update(metaDataDetails);
                boolean updated =  metaDataDao.update(metaData);

                if(updated) scacheMetaData(userToken, id, metaData.getMetaType());

                timePrinter.log();

                return updated;
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public boolean deleteMetadata(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [METADATA] [DELETE] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<MetaData> optMetaData = metaDataDao.findById(objectId);

            if(optMetaData.isPresent()) {
                boolean deleted = metaDataDao.delete(objectId);

                if(deleted) scacheMetaData(userToken, id, optMetaData.get().getMetaType());

                timePrinter.log();

                return deleted;
            }

            timePrinter.missing();
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

}
