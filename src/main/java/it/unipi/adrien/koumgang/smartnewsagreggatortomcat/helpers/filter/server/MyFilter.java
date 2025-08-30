package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Logs HTTP method, URI, and (for JSON/form requests) a copy of the body
 * without consuming the underlying input stream, so JAX-RS can still read it.
 */
@WebFilter("/*")
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init Server");
        // Ensure Mongo is initialized early
        MongoInstance.getInstance();
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        try {
            MongoInstance.getInstance().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Destroy Server");
    }

    private static boolean isBodyLoggingCandidate(String method, String contentType) {
        if (method == null) return false;
        boolean isMutation = "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);

        if (!isMutation) return false;
        if (contentType == null) return true; // be permissive

        String ct = contentType.toLowerCase();
        return ct.startsWith("application/json")
                || ct.startsWith("application/*+json")
                || ct.startsWith("application/x-www-form-urlencoded")
                || ct.startsWith("text/plain")
                || ct.startsWith("text/json");
    }

    private static String truncateForLog(String s, int maxBytes) {
        if (s == null) return null;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) return s;
        String cut = new String(bytes, 0, maxBytes, StandardCharsets.UTF_8);
        return cut + " …(truncated)…";
    }

    /**
     * Request wrapper that caches the body so it can be read multiple times.
     */
    private static final class CachedBodyHttpServletRequest extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final byte[] cachedBody;

        CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            try (InputStream in = request.getInputStream()) {
                this.cachedBody = in.readAllBytes();
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override public int read() { return bais.read(); }
                @Override public boolean isFinished() { return bais.available() == 0; }
                @Override public boolean isReady() { return true; }
                @Override public void setReadListener(ReadListener listener) { /* not async */ }
            };
        }

        @Override
        public BufferedReader getReader() {
            String enc = getCharacterEncoding();
            if (enc == null) enc = StandardCharsets.UTF_8.name();
            return new BufferedReader(new InputStreamReader(getInputStream(), Charset.forName(enc)));
        }

        @Override
        public int getContentLength() {
            return cachedBody.length;
        }

        @Override
        public long getContentLengthLong() {
            return cachedBody.length;
        }

        String getCachedBodyAsString() {
            String enc = getCharacterEncoding();
            if (enc == null) enc = StandardCharsets.UTF_8.name();
            return new String(cachedBody, Charset.forName(enc));
        }
    }
}
