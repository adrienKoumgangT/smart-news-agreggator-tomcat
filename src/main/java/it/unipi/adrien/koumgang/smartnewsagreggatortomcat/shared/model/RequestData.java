package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestData {

    /**
     * url: Optional[str]
     *     method: Optional[str]
     *     body: Optional[dict] = {}
     *     args: Optional[dict] = {}
     *     headers: Optional[dict] = {}
     *     form: Optional[dict] = {}
     * */

    @ModelField("url")
    private String url;

    @ModelField("method")
    private String method;

    @ModelField("body")
    private Map<String, Object> body;

    @ModelField("args")
    private Map<String, Object> args;

    @ModelField("headers")
    private Map<String, Object> headers;

    @ModelField("form")
    private Map<String, Object> form;


    // Constructors
    public RequestData() {
        this.body       = new HashMap<>();
        this.args       = new HashMap<>();
        this.headers    = new HashMap<>();
        this.form       = new HashMap<>();
    }

    public RequestData(String url, String method) {
        this();

        this.url    = url;
        this.method = method;
    }

    public RequestData(RequestDataView requestDataView) {
        this.url        = requestDataView.getUrl();
        this.method     = requestDataView.getMethod();
        this.body       = requestDataView.getBody();
        this.args       = requestDataView.getArgs();
        this.headers    = requestDataView.getHeaders();
        this.form       = requestDataView.getForm();
    }

    // Getters and Setters

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Map<String, Object> getBody() { return body; }
    public void setBody(Map<String, Object> body) { this.body = body; }

    public Map<String, Object> getArgs() { return args; }
    public void setArgs(Map<String, Object> args) { this.args = args; }

    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { this.headers = headers; }

    public Map<String, Object> getForm() { return form; }
    public void setForm(Map<String, Object> form) { this.form = form; }

    // Utility methods for map operations
    public void addBodyField(String key, Object value) {
        this.body.put(key, value);
    }

    public void addArg(String key, Object value) {
        this.args.put(key, value);
    }

    public void addHeader(String key, Object value) {
        this.headers.put(key, value);
    }

    public void addFormField(String key, Object value) {
        this.form.put(key, value);
    }

    public Optional<Object> getBodyField(String key) {
        return Optional.ofNullable(body.get(key));
    }

    public Optional<Object> getArg(String key) {
        return Optional.ofNullable(args.get(key));
    }

    public Optional<Object> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public Optional<Object> getFormField(String key) {
        return Optional.ofNullable(form.get(key));
    }

    public void removeBodyField(String key) {
        body.remove(key);
    }

    public void removeArg(String key) {
        args.remove(key);
    }

    public void removeHeader(String key) {
        headers.remove(key);
    }

    public void removeFormField(String key) {
        form.remove(key);
    }

    public boolean containsBodyField(String key) {
        return body.containsKey(key);
    }

    public boolean containsArg(String key) {
        return args.containsKey(key);
    }

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    public boolean containsFormField(String key) {
        return form.containsKey(key);
    }

    public void clearBody() {
        body.clear();
    }

    public void clearArgs() {
        args.clear();
    }

    public void clearHeaders() {
        headers.clear();
    }

    public void clearForm() {
        form.clear();
    }
}
