package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.service.ServerEventLogService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view.ServerEventLogView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server.ServerUtils;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.token.TokenManager;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.ApiConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.InvalidAuthentificationException;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe.UnauthorizedException;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


@Provider
public class ServerExceptionMapper implements ExceptionMapper<Throwable> {

    public static Boolean DEBUG;

    static {
        try {
            ApiConfiguration apiConfiguration = new ApiConfiguration();
            DEBUG = apiConfiguration.isDebug();
        } catch (Exception e) {
            DEBUG = false;
        }
    }

    // Inject request information
    @Context
    private ContainerRequestContext request;

    @Context
    private HttpHeaders headers;

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable throwable) {
        if(DEBUG) throwable.printStackTrace();


        if(!skipException(throwable) ) {
            if(request != null) {

                // Extract Request Info
                String method = request.getMethod();
                String url = uriInfo.getRequestUri().toString();
                Map<String, List<String>> headerMap = headers.getRequestHeaders();
                MultivaluedMap<String, String> paramsMap = uriInfo.getQueryParameters();

                String requestBody;
                if (ServerUtils.shouldSkipBodyLog(method, request.getMediaType())) {
                    requestBody = null;
                } else {
                    requestBody = (String) request.getProperty(ServerUtils.RAW_BODY_PROP);
                }

                // Extract Status Code
                int statusCode = (throwable instanceof UnauthorizedException) ? 401 : 500;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                String errorMessage = sw.toString();

                // Build cURL Command
                StringJoiner curlHeaders = new StringJoiner(" ");
                headerMap.forEach((key, values) -> values.forEach(value ->
                        curlHeaders.add("-H '" + key + ": " + value + "'")
                ));

                String curlCommand = String.format(
                        "curl -X %s %s %s -d '%s'",
                        method, curlHeaders, url, requestBody
                );

                try {
                    String idUser = null;
                    if(statusCode != 401 && headerMap.containsKey(HttpHeaders.AUTHORIZATION)) {
                        String authorization = headerMap.get(HttpHeaders.AUTHORIZATION).getFirst();
                        try {
                            UserToken userToken = TokenManager.readToken(authorization);
                            idUser = userToken.getIdUser();
                        } catch (Exception ignored) { }
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

                    RequestDataView requestDataView = new RequestDataView(
                            url,
                            method,
                            mapBody,
                            new HashMap<>(paramsMap),
                            new HashMap<>(headerMap),
                            new HashMap<>()
                    );

                    ServerEventLogView serverEventLogView = ServerEventLogService.getInstance()
                            .saveServerEventLog(
                                    "exception",
                                    throwable.getClass().getSimpleName(),
                                    errorMessage,
                                    null,
                                    curlCommand,
                                    requestDataView,
                                    idUser
                            );

                } catch (Exception ignored) { }
            }
        }


        return (throwable instanceof UnauthorizedException || throwable instanceof InvalidAuthentificationException)
                ? ApiResponseController.unauthorized(throwable.getMessage())
                : ApiResponseController.error(throwable.getMessage())
                ;
    }


    private static boolean skipException(Throwable throwable) {
        return (
                (throwable instanceof SafeException)
                        || (throwable instanceof jakarta.ws.rs.NotFoundException)
                        || (throwable instanceof jakarta.ws.rs.NotAllowedException)
        );
    }

}
