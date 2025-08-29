package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.CacheSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CacheHandlerMap<K, V> implements CacheHandlerInterface<K, V> {

    private final Map<K, String> cache = new ConcurrentHashMap<>();
    private final Map<K, String> typeMap = new ConcurrentHashMap<>();
    private final Map<K, Long> expiryMap = new ConcurrentHashMap<>();

    private final File storageFile;
    private final CacheSerializer serializer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long saveIntervalSeconds;

    public CacheHandlerMap(String persistencePath, CacheSerializer serializer) {
        this(persistencePath, serializer, 30); // default: save every 30s
    }

    public CacheHandlerMap(String persistencePath, CacheSerializer serializer, long saveIntervalSeconds) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[CACHE] [HANDLER MAP] [CONSTRUCTOR] Persistence Path: " + (persistencePath != null ? persistencePath : "null")
                        + " --- Serializer : " + serializer.toString()
                        + " --- Save interval : " + saveIntervalSeconds + " seconds"
        );

        this.serializer = serializer;
        this.storageFile = persistencePath != null && !persistencePath.isBlank() ? new File(persistencePath) : null;
        this.saveIntervalSeconds = saveIntervalSeconds;

        if (this.storageFile != null) {
            loadFromDisk();
            setupPeriodicSave();
            setupShutdownHook();
        }

        timePrinter.log();
    }

    @Override
    public V get(K key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [GET] key: " + key.toString());

        if (isExpired(key)) {
            delete(key);
            timePrinter.log();
            return null;
        }

        String json = cache.get(key);
        String typeName = typeMap.get(key);
        if (json == null || typeName == null){timePrinter.log(); return null;}

        try {
            Class<?> clazz = Class.forName(typeName);
            if (isPrimitiveOrString(clazz)) {
                timePrinter.log();
                return castPrimitive(json, clazz);
            }
            timePrinter.log();
            return serializer.deserialize(json, (Class<V>) clazz);
        } catch (Exception e) {
            // throw new RuntimeException("Deserialization failed for key: " + key, e);
            timePrinter.error("Deserialization failed for key: " + key + " --- exception: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void set(K key, V value) {
        set(key, value, -1);
    }

    @Override
    public void set(K key, V value, long ttlInSeconds) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [SET] key: " + key.toString());

        String valueStr;
        Class<?> valueClass = value.getClass();
        String className = valueClass.getName();

        if (isPrimitiveOrString(valueClass)) {
            valueStr = String.valueOf(value);
        } else {
            valueStr = serializer.serialize(value);
        }

        cache.put(key, valueStr);
        typeMap.put(key, className);

        if (ttlInSeconds > 0) {
            expiryMap.put(key, System.currentTimeMillis() + ttlInSeconds * 1000);
        } else {
            expiryMap.remove(key);
        }

        timePrinter.log();
    }

    @Override
    public void delete(K key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [DELETE] key: " + key.toString());

        cache.remove(key);
        typeMap.remove(key);
        expiryMap.remove(key);

        timePrinter.log();
    }

    @Override
    public boolean contains(K key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [CONTAINS] key: " + key.toString());

        if (isExpired(key)) {
            delete(key);
            // timePrinter.log();
            return false;
        }
        // timePrinter.log();
        return cache.containsKey(key);
    }

    private boolean isExpired(K key) {
        Long expiry = expiryMap.get(key);
        return expiry != null && System.currentTimeMillis() > expiry;
    }

    private boolean isPrimitiveOrString(Class<?> cls) {
        return cls.isPrimitive() ||
                cls == String.class ||
                cls == Integer.class ||
                cls == Long.class ||
                cls == Double.class ||
                cls == Float.class ||
                cls == Boolean.class ||
                cls == Short.class ||
                cls == Byte.class ||
                cls == Character.class;
    }

    @SuppressWarnings("unchecked")
    private V castPrimitive(String value, Class<?> type) {
        Object result;
        if (type == String.class) result = value;
        else if (type == Integer.class) result = Integer.valueOf(value);
        else if (type == Long.class) result = Long.valueOf(value);
        else if (type == Double.class) result = Double.valueOf(value);
        else if (type == Float.class) result = Float.valueOf(value);
        else if (type == Boolean.class) result = Boolean.valueOf(value);
        else if (type == Short.class) result = Short.valueOf(value);
        else if (type == Byte.class) result = Byte.valueOf(value);
        else if (type == Character.class) result = value.charAt(0);
        else throw new IllegalArgumentException("Unsupported primitive type: " + type);
        return (V) result;
    }

    private void saveToDisk() {
        if (storageFile == null) return;

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [SAVE TO DISK] storage File: " + storageFile.getAbsolutePath());

        Map<String, Object> saveObj = new HashMap<>();
        saveObj.put("cache", cache);
        saveObj.put("typeMap", typeMap);
        saveObj.put("expiryMap", expiryMap);

        // long start = System.currentTimeMillis();

        try (FileOutputStream fos = new FileOutputStream(storageFile);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos);
             OutputStreamWriter writer = new OutputStreamWriter(gzipOut)) {

            // Save using Gson or Jackson
            writer.write(serializer.serialize(saveObj));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // long timeTaken = System.currentTimeMillis() - start;
        // System.out.println("[CacheHandlerMap] Saved to disk in " + timeTaken + "ms (" + storageFile.length() + " bytes)");

        timePrinter.log();
    }

    private void loadFromDisk() {
        if (storageFile == null || !storageFile.exists()) return;

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER MAP] [LOADED FROM DISK] storage File: " + storageFile.getAbsolutePath());

        // long start = System.currentTimeMillis();

        try (FileInputStream fis = new FileInputStream(storageFile);
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             InputStreamReader reader = new InputStreamReader(gzipIn)) {

            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[4096];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }

            String json = sb.toString();
            Map<String, Object> saved = serializer.deserialize(json, Map.class);

            cache.putAll((Map<K, String>) saved.get("cache"));
            typeMap.putAll((Map<K, String>) saved.get("typeMap"));

            Map<K, Double> rawExpiry = (Map<K, Double>) saved.get("expiryMap");
            rawExpiry.forEach((k, v) -> expiryMap.put(k, v.longValue()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // long timeTaken = System.currentTimeMillis() - start;
        // System.out.println("[CacheHandlerMap] Loaded from disk in " + timeTaken + "ms");

        timePrinter.log();
    }

    private void setupPeriodicSave() {
        scheduler.scheduleAtFixedRate(this::saveToDisk, saveIntervalSeconds, saveIntervalSeconds, TimeUnit.SECONDS);
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveToDisk));
    }
}

