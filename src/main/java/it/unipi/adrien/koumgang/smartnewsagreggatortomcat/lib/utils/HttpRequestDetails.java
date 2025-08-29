package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

import java.util.Map;

public class HttpRequestDetails {

    private final String method;
    private final String requestURI;
    private final String clientIp;
    private final Map<String, String[]> parameters;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequestDetails(HttpRequestDetailsBuilder builder) {
        this.method = builder.method;
        this.requestURI = builder.requestURI;
        this.clientIp = builder.clientIp;
        this.parameters = builder.parameters;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getClientIp() {
        return clientIp;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static HttpRequestDetailsBuilder builder() {
        return new HttpRequestDetailsBuilder();
    }

    public static class HttpRequestDetailsBuilder {
        private String method;
        private String requestURI;
        private String clientIp;
        private Map<String, String[]> parameters;
        private Map<String, String> headers;
        private String body;

        public HttpRequestDetailsBuilder() {}

        public HttpRequestDetailsBuilder method(String method) {
            this.method = method;
            return this;
        }

        public HttpRequestDetailsBuilder requestURI(String requestURI) {
            this.requestURI = requestURI;
            return this;
        }

        public HttpRequestDetailsBuilder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public HttpRequestDetailsBuilder parameters(Map<String, String[]> parameters) {
            this.parameters = parameters;
            return this;
        }

        public HttpRequestDetailsBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequestDetailsBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestDetails build() {
            return new HttpRequestDetails(this);
        }
    }

}
