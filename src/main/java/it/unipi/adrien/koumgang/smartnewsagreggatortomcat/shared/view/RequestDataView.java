package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;

import java.util.Map;

public class RequestDataView {

    private String url;

    private String method;

    private Map<String, Object> body;

    private Map<String, Object> args;

    private Map<String, Object> headers;

    private Map<String, Object> form;

    public RequestDataView() {}

    public RequestDataView(
            String url,
            String method,
            Map<String, Object> body,
            Map<String, Object> args,
            Map<String, Object> headers,
            Map<String, Object> form
    ) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.args = args;
        this.headers = headers;
        this.form = form;
    }

    public RequestDataView(RequestData requestData) {
        this.url        = requestData.getUrl();
        this.method     = requestData.getMethod();
        this.body       = requestData.getBody();
        this.args       = requestData.getArgs();
        this.headers    = requestData.getHeaders();
        this.form       = requestData.getForm();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getForm() {
        return form;
    }

    public void setForm(Map<String, Object> form) {
        this.form = form;
    }
}
