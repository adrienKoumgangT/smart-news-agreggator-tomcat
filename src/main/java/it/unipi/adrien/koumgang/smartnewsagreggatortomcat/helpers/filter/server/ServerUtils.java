package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerUtils {

    public static final String RAW_BODY_PROP = "rawBody";
    public static final int MAX_LOG_BODY_BYTES = 10_000;

    public static boolean shouldSkipBodyLog(String method, MediaType mt) {
        if (method == null) return true;
        boolean mutation = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);
        if (!mutation) return true;
        if (mt == null) return false;
        String full = (mt.getType() + "/" + mt.getSubtype()).toLowerCase();
        return !full.startsWith("application/json")
                && !full.startsWith("application/*+json")
                && !full.startsWith("application/x-www-form-urlencoded")
                && !full.startsWith("text/plain")
                && !full.startsWith("text/json");
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



    public static RequestDataView getRequestDataView(ContainerRequestContext request, HttpHeaders headers, UriInfo uriInfo) {
        if(request == null) return null;

        String method = request.getMethod();
        String url = uriInfo.getRequestUri().toString();
        Map<String, List<String>> headerMap = headers.getRequestHeaders();
        MultivaluedMap<String, String> paramsMap = uriInfo.getQueryParameters();

        String requestBody;
        if (shouldSkipBodyLog(method, request.getMediaType())) {
            requestBody = null;
        } else {
            requestBody = (String) request.getProperty(RAW_BODY_PROP);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> mapBody;
        try {
            if (requestBody == null || requestBody.isBlank() || Objects.equals(requestBody, "N/A")) mapBody = new HashMap<>();
            else mapBody = gson.fromJson(requestBody, mapType);
        } catch (Exception ignored) {
            mapBody = new HashMap<>();
        }

        return new RequestDataView(
                url,
                method,
                mapBody,
                new HashMap<>(paramsMap),
                new HashMap<>(headerMap),
                new HashMap<>()
        );
    }

}
