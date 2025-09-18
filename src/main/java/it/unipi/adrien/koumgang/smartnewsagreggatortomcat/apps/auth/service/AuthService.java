package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserStatus;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.filter.server.ServerUtils;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordGenerator;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.token.TokenManager;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.utils.AuthUtils;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    public static AuthService getInstance() {
        return new AuthService();
    }


    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    private final AuthEventLogService authEventLogService;
    private final UserService userService;

    public AuthService() {
        authEventLogService = AuthEventLogService.getInstance();
        userService = UserService.getInstance();
    }


    public boolean checkUsernameAvailable(String username) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH] [CHECK USERNAME AVAILABLE] username: " + username);

        Optional<User> optUser = userService.getUserByUsername(username);

        timePrinter.log();

        return optUser.isEmpty();
    }


    public UserView registration(
            ContainerRequestContext request,
            HttpHeaders headers,
            UriInfo uriInfo,
            UserView userViewParam,
            String password,
            String metaUser
    ) {
        String event = "registration";

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH] [LOGIN] username: " + gson.toJson(userViewParam));

        String ip = headers == null ? null : AuthUtils.getIpFromRequestHeader(headers);
        Map<String, List<String>> dataServer = headers == null ? new HashMap<>() : AuthUtils.getDataServer(headers);

        RequestDataView requestDataView;
        if(request != null && headers != null && uriInfo != null) {
            requestDataView = ServerUtils.getRequestDataView(request, headers, uriInfo);
        } else {
            requestDataView = null;
        }

        // Optional<User> optUser = userService.getUserByUsername(userViewParam.getUsername());
        Optional<User> optUser = userService.getUserByEmail(userViewParam.getEmail());
        if(optUser.isPresent()) {
            authEventLogService.saveAuthEventLog(
                    event,
                    // "User with username " + userViewParam.getUsername() + " already exists",
                    "User with email " + userViewParam.getEmail() + " already exists",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    metaUser
            );

            timePrinter.missing("User with username " + userViewParam.getUsername() + " already exists");

            return null;
        }


        User user = new User(userViewParam);
        user.setAdmin(false);
        user.setStatus(UserStatus.PENDING.getCode());
        if(password != null) {
            user.setPassword(password);
        } else {
            user.setPassword(PasswordGenerator.generate());
            user.getPassword().setNeedChange(true);
        }

        UserView userView = userService.saveUser(user);

        if(userView == null) {
            authEventLogService.saveAuthEventLog(
                    event,
                    "Failed to save user",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    metaUser
            );

            timePrinter.missing("Failed to save user");

            return null;
        }

        authEventLogService.saveAuthEventLog(
                event,
                "Successfully registered user",
                true,
                requestDataView,
                null,
                ip,
                dataServer,
                metaUser
        );

        timePrinter.log();

        return userView;
    }



    public String login(String username, String password) {
        return login(null, null, null, username, password);
    }

    public String login(ContainerRequestContext request, HttpHeaders headers, UriInfo uriInfo, String username, String password) {
        String event = "login";

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH] [LOGIN] username: " + username);

        String ip = headers == null ? null : AuthUtils.getIpFromRequestHeader(headers);
        String userAgent = headers == null ? null : AuthUtils.getUserAgentFromRequestHeader(headers);
        Map<String, List<String>> dataServer = headers == null ? new HashMap<>() : AuthUtils.getDataServer(headers);

        RequestDataView requestDataView;
        if(request != null && headers != null && uriInfo != null) {
            requestDataView = ServerUtils.getRequestDataView(request, headers, uriInfo);
        } else {
            requestDataView = null;
        }

        // Optional<User> optUser = userService.getUserByUsername(username);
        Optional<User> optUser = userService.getUserByEmail(username);

        if(optUser.isEmpty()) {
            authEventLogService.saveAuthEventLog(
                    event,
                    "user not found",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    null
            );

            timePrinter.missing("User not found");

            return null;
        }

        User user = optUser.get();

        if(!user.canLogin()) {
            authEventLogService.saveAuthEventLog(
                    event,
                    "User can't login",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    null
            );

            userService.recordFailedLogin(
                    StringIdConverter.getInstance().fromObjectId(user.getUserId()),
                    ip, userAgent,
                    false
            );

            timePrinter.missing("User can't login");

            return null;
        }

        boolean correctPassword = user.verifyPassword(password);

        if(!correctPassword) {
            authEventLogService.saveAuthEventLog(
                    event,
                    "Invalid password",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    null
            );

            userService.recordFailedLogin(
                    StringIdConverter.getInstance().fromObjectId(user.getUserId()),
                    ip, userAgent,
                    true
            );

            timePrinter.missing("Invalid password");

            return null;
        }

        UserToken userToken = new UserToken(user);
        String token = TokenManager.createJWTAndSign(userToken);

        authEventLogService.saveAuthEventLog(
                event,
                "Successfully logged in",
                true,
                requestDataView,
                token,
                ip,
                dataServer,
                null
        );

        userService.recordSuccessLogin(
                StringIdConverter.getInstance().fromObjectId(user.getUserId()),
                ip, userAgent,
                false
        );

        timePrinter.log();

        return token;
    }

    // TODO: to delete
    public String loginAlt(ContainerRequestContext request, HttpHeaders headers, UriInfo uriInfo, String username, String password) {
        String event = "login";

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH] [LOGIN] username: " + username);

        String ip = headers == null ? null : AuthUtils.getIpFromRequestHeader(headers);
        String userAgent = headers == null ? null : AuthUtils.getUserAgentFromRequestHeader(headers);
        Map<String, List<String>> dataServer = headers == null ? new HashMap<>() : AuthUtils.getDataServer(headers);

        RequestDataView requestDataView;
        if(request != null && headers != null && uriInfo != null) {
            requestDataView = ServerUtils.getRequestDataView(request, headers, uriInfo);
        } else {
            requestDataView = null;
        }

        // Optional<User> optUser = userService.getUserByUsername(username);
        Optional<User> optUser = userService.getUserByEmail(username);

        if(optUser.isEmpty()) {
            timePrinter.missing("User not found");

            authEventLogService.saveAuthEventLog(
                    event,
                    "user not found",
                    false,
                    requestDataView,
                    null,
                    ip,
                    dataServer,
                    null
            );

            return null;
        }

        User user = optUser.get();

        UserToken userToken = new UserToken(user);
        String token = TokenManager.createJWTAndSign(userToken);

        authEventLogService.saveAuthEventLog(
                event,
                "Invalid password",
                false,
                requestDataView,
                token,
                ip,
                dataServer,
                null
        );

        userService.recordSuccessLogin(
                StringIdConverter.getInstance().fromObjectId(user.getUserId()),
                ip, userAgent,
                false
        );

        timePrinter.log();

        return token;
    }

}
