package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.service.AuthService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.LoginView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.DataBoolean;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.DataString;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;


@Path("/auth")
@Tag(name = "Authentication", description = "API operation related to authentication")
public class AuthController extends BaseController {


    @POST
    @Path("username/available")
    @Operation(summary = "Check If username is available", description = "Return true if username is available else false")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = DataBoolean.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUsernameAvailable(
            DataString dataString
    ) throws Exception {
        dataString.checkIfValid();

        boolean available = AuthService.getInstance().checkUsernameAvailable(dataString.getData());

        return ApiResponseController.ok(new DataBoolean(available));
    }

    @PUT
    @Path("registration")
    @Operation(summary = "Registration", description = "Create new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registration(
            @Context ContainerRequestContext request,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo,
            RegisterView registerView
    ) throws Exception {
        registerView.checkIfValid();

        UserView userView = new UserView(registerView);

        UserView user = AuthService.getInstance()
                .registration(
                        request, headers, uriInfo,
                        userView, registerView.getPassword(),
                        null
                );

        if(user == null) {
            return ApiResponseController.error("Registration failed");
        }

        return ApiResponseController.ok();
    }

    @POST
    @Path("login")
    @Operation(summary = "Login", description = "Return a Authorization token in header")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @Context ContainerRequestContext request,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo,
            LoginView loginView
    ) throws Exception {
        loginView.checkIfValid();

        AuthService authService = AuthService.getInstance();

        String token = authService.login(request, headers, uriInfo, loginView.getEmail(), loginView.getPassword());

        if(token == null) {
            return ApiResponseController.error("Invalid username/password");
        }

        return ApiResponseController.okWithHeader("Authorization", "Bearer " + token);
    }

    @POST
    @Path("login-alt")
    @Operation(summary = "Put a Test instance", description = "Update a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginAlt(
            @Context ContainerRequestContext request,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo,
            LoginView loginView
    ) throws Exception {
        loginView.checkIfValid();

        AuthService authService = AuthService.getInstance();

        String token = authService.loginAlt(request, headers, uriInfo, loginView.getEmail(), loginView.getPassword());

        if(token == null) {
            return ApiResponseController.error("Invalid username/password");
        }

        return ApiResponseController.okWithHeader("Authorization", "Bearer " + token);
    }


    @GET
    @Path("logout")
    @Operation(summary = "Make token invalid", description = "Make the token of the current user invalid for api call")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("Authorization") String token) throws Exception {
        UserToken user = getUserToken(token);

        // TODO: make the token this current user invalid

        return ApiResponseController.ok();
    }

    @GET
    @Path("me")
    @Operation(summary = "Get a me information", description = "Get information about user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = UserToken.class)) }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response me(@HeaderParam("Authorization") String token) throws Exception {
        UserToken user = getUserToken(token);

        return ApiResponseController.ok(user);
    }

}
