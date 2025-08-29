package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller;

import jakarta.ws.rs.core.Response;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ApiResponseController {

    //--------------
    // CODE 200 : OK
    //--------------

    /**
     * The request succeeded.
     *
     * @return Response with OK status.
     */
    public static Response ok() {
        return ok("");
    }

    /**
     * The request succeeded.
     *
     * @param content the content of the client's request
     * @return Response with OK status.
     */
    public static Response ok(Object content) {
        return ok(content, "");
    }

    /**
     * The request succeeded.
     *
     * @param content the content of the client's request
     * @param message message to send to the client.
     * @return Response with OK status.
     */
    public static Response ok(Object content, String message) {
        return Response
                .ok()
                .header("message", message)
                .entity(content)
                .build();
    }

    /**
     * The request succeeded.
     *
     * @param header the header to add in header response.
     * @param value the value of header.
     * @return Response with OK status and header.
     * */
    public static Response okWithHeader(String header, Object value) {
        return Response
                .ok()
                .header(header, value)
                .build();
    }

    public static Response fileResponse(byte[] content, String fileName, String mimeType) {
        return Response.ok(content)
                .header("Content-Disposition", "attachment; filename=\""+fileName+ "\"")
                .header("Content-Type", mimeType)
                .header("Content-Length", content.length)
                .build();
    }

    public static Response fileResponseAsBase64(byte[] content, String fileName, String mimeType) {
        String base64 = Base64.getEncoder().encodeToString(content);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("fileName", fileName);
        responseBody.put("contentType", mimeType);
        responseBody.put("isBase64Encoded", true);
        responseBody.put("body", base64);
        responseBody.put("Content-Disposition", "attachment; filename=\""+fileName+ "\"");

        return Response.ok(responseBody)
                .header("Content-Type", "application/json")
                .build();
    }

    //--------------
    // CODE 201 : CREATED
    //--------------

    /**
     * The request has succeeded and a new resource has been created as a result.
     *
     * @return Response with CREATED status.
     */
    public static Response created() {
        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    public static Response created(Object content) {
        return Response
                .status(Response.Status.CREATED)
                .entity(content)
                .build();
    }

    public static Response created(Object content, String message) {
        return Response
                .status(Response.Status.CREATED)
                .header("message", message)
                .entity(content)
                .build();
    }

    //--------------
    // CODE 204 : NO CONTENT
    //--------------

    /**
     * The server has successfully fulfilled the request and there is no content to send in the response.
     *
     * @return Response with NO CONTENT status.
     */
    public static Response noContent() {
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    //--------------
    // CODE 301 : MOVED PERMANENTLY
    //--------------

    /**
     * The resource has been moved permanently to a new URI.
     *
     * @param newUri the URI to redirect to
     * @return Response with MOVED PERMANENTLY status.
     */
    public static Response movedPermanently(String newUri) {
        return Response
                .status(Response.Status.MOVED_PERMANENTLY)
                .header("Location", newUri)
                .build();
    }

    //--------------
    // CODE 302 : FOUND (TEMPORARY REDIRECT)
    //--------------

    /**
     * The resource resides temporarily under a different URI.
     *
     * @param newUri the URI to redirect to
     * @return Response with FOUND status.
     */
    public static Response found(String newUri) {
        return Response
                .status(Response.Status.FOUND)
                .header("Location", newUri)
                .build();
    }

    //--------------
    // CODE 303 : SEE OTHER
    //--------------

    /**
     * The server is redirecting the client to a different URI.
     *
     * @param newUri the URI to redirect to
     * @return Response with SEE OTHER status.
     */
    public static Response seeOther(String newUri) {
        return Response
                .status(Response.Status.SEE_OTHER)
                .header("Location", newUri)
                .build();
    }

    //--------------
    // CODE 307 : TEMPORARY REDIRECT
    //--------------

    /**
     * The requested resource resides temporarily under a different URI.
     *
     * @param newUri the URI to redirect to
     * @return Response with TEMPORARY REDIRECT status.
     */
    public static Response temporaryRedirect(String newUri) {
        return Response
                .status(Response.Status.TEMPORARY_REDIRECT)
                .header("Location", newUri)
                .build();
    }

    //--------------
    // CODE 308 : PERMANENT REDIRECT
    //--------------

    /**
     * The request and all future requests should be repeated using another URI.
     *
     * @param newUri the URI to redirect to
     * @return Response with PERMANENT REDIRECT status.
     */
    public static Response permanentRedirect(String newUri) {
        return Response
                .status(Response.Status.PERMANENT_REDIRECT)
                .header("Location", newUri)
                .build();
    }

    //--------------
    // CODE 400 : BAD REQUEST
    //--------------

    /**
     * The server cannot or will not process the request due to something that is perceived to be a client error
     * (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).
     *
     * @return Response with BAD REQUEST status.
     */
    public static Response badRequest() {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .build();
    }

    /**
     * The server cannot or will not process the request due to something that is perceived to be a client error
     * (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).
     *
     * @param message message to send to the client.
     * @return Response with BAD REQUEST status.
     */
    public static Response badRequest(String message) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 401 : UNAUTHORIZED
    //--------------

    /**
     * That is, the client must authenticate itself to get the requested response.
     *
     * @return Response with UNAUTHORIZED status.
     */
    public static Response unauthorized() {
        return unauthorized("");
    }

    /**
     * That is, the client must authenticate itself to get the requested response.
     *
     * @param message message to send to the client.
     * @return Response with UNAUTHORIZED status.
     */
    public static Response unauthorized(String message) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 403 : FORBIDDEN
    //--------------

    /**
     * The client does not have access rights to the content;
     * that is, it is unauthorized, so the server is refusing to give the requested resource.
     *
     * @return Response with FORBIDDEN status.
     */
    public static Response forbidden() {
        return forbidden("");
    }

    /**
     * The client does not have access rights to the content;
     * that is, it is unauthorized, so the server is refusing to give the requested resource.
     *
     * @param message message to send to the client.
     * @return Response with FORBIDDEN status.
     */
    public static Response forbidden(String message) {
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 404 : NOT FOUND
    //--------------

    /**
     * The endpoint is valid but the resource itself does not exist.
     *
     * @return Response with NOT FOUND status.
     */
    public static Response notFound() {
        return notFound("");
    }

    /**
     * The endpoint is valid but the resource itself does not exist.
     *
     * @param message message to send to the client.
     * @return Response with NOT FOUND status.
     */
    public static Response notFound(String message) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 409 : CONFLICT
    //--------------

    /**
     * The request could not be completed due to a conflict with the current state of the target resource.
     */
    public static Response conflict(String message) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 422 : UNPROCESSABLE ENTITY
    //--------------

    /**
     * The request was well-formed but was unable to be followed due to semantic errors.
     */
    public static Response unprocessableEntity(String message) {
        return Response
                .status(422)
                .entity(message)
                .build();
    }

    //--------------
    // CODE 500 : INTERNAL SERVER ERROR
    //--------------

    /**
     * The server has encountered a situation it does not know how to handle.
     *
     * @return Response with Internal Server Error status.
     */
    public static Response error() {
        return error("");
    }

    /**
     * The server has encountered a situation it does not know how to handle.
     *
     * @param message message to send to the client.
     * @return Response with Internal Server Error status.
     */
    public static Response error(String message) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(message)
                .build();
    }

    //--------------
    // MessageServer constants
    //--------------

    public static class MessageServer {

        private static final String MSG_ERROR_INVALID_TOKEN_AUTHENTICATION = "Invalid token authentication";
        private static final String MSG_ERROR_ELEMENT_NOT_FOUND = "Element not found";
        private static final String MSG_ERROR_NO_SAVE = "Error during saving";
        private static final String MSG_ERROR_CONFLICT = "Resource already exists";
        private static final String MSG_ERROR_UNPROCESSABLE = "Unprocessable entity";
        private static final String MSG_ERROR_FORBIDDEN = "Access denied";

        private static final String MSG_SUCCESS_DELETE = "Successfully deleted";
        private static final String MSG_SUCCESS_CREATED = "Successfully created";
        private static final String MSG_SUCCESS_UPDATED = "Successfully updated";

        public static String invalidTokenAuthentication() {
            return MSG_ERROR_INVALID_TOKEN_AUTHENTICATION;
        }

        public static String elementNotFound() {
            return MSG_ERROR_ELEMENT_NOT_FOUND;
        }

        public static String saveError() {
            return MSG_ERROR_NO_SAVE;
        }

        public static String conflict() {
            return MSG_ERROR_CONFLICT;
        }

        public static String unprocessableEntity() {
            return MSG_ERROR_UNPROCESSABLE;
        }

        public static String forbidden() {
            return MSG_ERROR_FORBIDDEN;
        }

        public static String deletedSuccessfully() {
            return MSG_SUCCESS_DELETE;
        }

        public static String createdSuccessfully() {
            return MSG_SUCCESS_CREATED;
        }

        public static String updatedSuccessfully() {
            return MSG_SUCCESS_UPDATED;
        }
    }
}