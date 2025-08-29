package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server;

import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;

public class ServerUtils {

    public static final String RAW_BODY_PROP = "rawBody";
    public static final int MAX_LOG_BODY_BYTES = 10_000;

    public static boolean shouldLogBody(String method, MediaType mt) {
        if (method == null) return false;
        boolean mutation = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);
        if (!mutation) return false;
        if (mt == null) return true;
        String full = (mt.getType() + "/" + mt.getSubtype()).toLowerCase();
        return full.startsWith("application/json")
                || full.startsWith("application/*+json")
                || full.startsWith("application/x-www-form-urlencoded")
                || full.startsWith("text/plain")
                || full.startsWith("text/json");
    }

    public static byte[] readAll(InputStream in) throws IOException {
        // Small, dependency-free readAllBytes for Java 8 compatibility
        byte[] buffer = new byte[8192];
        int n;
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            while ((n = in.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }

}
