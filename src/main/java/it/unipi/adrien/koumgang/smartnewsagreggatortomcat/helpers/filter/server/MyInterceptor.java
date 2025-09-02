package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Logs HTTP method, URI, and (for JSON/form requests) a copy of the body
 * without consuming the underlying input stream, so JAX-RS can still read it.
 */
@Provider
public class MyInterceptor implements ContainerRequestFilter, ContainerResponseFilter {



    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        // String header = new GsonBuilder().serializeNulls().create().toJson(containerRequestContext.getHeaders());
        // String method = containerRequestContext.getMethod();
        // System.out.println("[Request] method: " + method + " --- header: " + header);


        final String method = request.getMethod();
        final String path = request.getUriInfo() != null ? request.getUriInfo().getPath() : "";

        if (ServerUtils.shouldSkipBodyLog(method, request.getMediaType())) {
            MineLog.blue("[HTTP] " + method + " --- URI: /" + path);
            return; // don’t touch the stream
        }

        // Read the request entity stream fully
        byte[] bodyBytes = ServerUtils.readAll(request.getEntityStream());
        // store for later (ExceptionMapper, etc.)
        request.setProperty(ServerUtils.RAW_BODY_PROP, new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8));
        // restore for normal processing
        // Reset entity stream so JAX-RS/Jackson can read it later
        request.setEntityStream(new ByteArrayInputStream(bodyBytes));

        // Prepare a safe preview for logging
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        if (bodyBytes.length > ServerUtils.MAX_LOG_BODY_BYTES) {
            body = new String(bodyBytes, 0, ServerUtils.MAX_LOG_BODY_BYTES, StandardCharsets.UTF_8) + " …(truncated)…";
        }

        // Print your log (replace with your logger)
        // System.out.printf("[HTTP] %s --- URI: /%s --- Body: %s%n", method, path, body);
        MineLog.blue("[HTTP] " + method + " --- URI: /" + path + " --- Body: " + body);
    }



    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        // int statusResponse = response.getStatus();
        // String header = new GsonBuilder().serializeNulls().create().toJson(response.getHeaders());
        // System.out.println("[Response] status: " + statusResponse + " --- header: " + header);
    }

}
