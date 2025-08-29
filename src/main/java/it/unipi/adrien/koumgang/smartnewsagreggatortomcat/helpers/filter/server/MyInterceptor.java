package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server.ServerUtils.readAll;
import static it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server.ServerUtils.shouldLogBody;

@Provider
public class MyInterceptor implements ContainerRequestFilter, ContainerResponseFilter {



    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        // String header = new GsonBuilder().serializeNulls().create().toJson(containerRequestContext.getHeaders());
        // String method = containerRequestContext.getMethod();
        // System.out.println("[Request] method: " + method + " --- header: " + header);


        final String method = ctx.getMethod();
        final String path = ctx.getUriInfo() != null ? ctx.getUriInfo().getPath() : "";

        if (!shouldLogBody(method, ctx.getMediaType())) {
            return; // don’t touch the stream
        }

        // Read the request entity stream fully
        byte[] bodyBytes = readAll(ctx.getEntityStream());
        // store for later (ExceptionMapper, etc.)
        ctx.setProperty(ServerUtils.RAW_BODY_PROP, new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8));
        // restore for normal processing
        // Reset entity stream so JAX-RS/Jackson can read it later
        ctx.setEntityStream(new ByteArrayInputStream(bodyBytes));

        // Prepare a safe preview for logging
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        if (bodyBytes.length > ServerUtils.MAX_LOG_BODY_BYTES) {
            body = new String(bodyBytes, 0, ServerUtils.MAX_LOG_BODY_BYTES, StandardCharsets.UTF_8) + " …(truncated)…";
        }

        // Print your log (replace with your logger)
        // System.out.printf("[HTTP] %s --- URI: /%s --- Body: %s%n", method, path, body);
        new MineLog.TimePrinter("[HTTP] " + method + " --- URI: /" + path + " --- Body: " + body);
    }



    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        // int statusResponse = containerResponseContext.getStatus();
        // String header = new GsonBuilder().serializeNulls().create().toJson(containerResponseContext.getHeaders());
        // System.out.println("[Response] status: " + statusResponse + " --- header: " + header);
    }

}
