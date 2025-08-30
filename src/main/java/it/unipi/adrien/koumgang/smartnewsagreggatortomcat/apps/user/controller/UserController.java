package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.service.AuthService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.ListUserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserMeView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

import java.util.List;

@Path("/user")
@Tag(name = "Users", description = "API operation related to users")
public class UserController extends BaseController {


    @GET
    @Operation(summary = "Get list of a User instance", description = "Return a list of User instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("Authorization") String token) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        List<UserView> users = UserService.getInstance().listUsers(userToken);

        return ApiResponseController.ok(users);
    }


    @GET
    @Path("list")
    @Operation(summary = "Get list of a User instance", description = "Return paginated list of User instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ListUserView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersWithPagination(
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("1") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10")
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        List<UserView> users = UserService.getInstance().listUsers(userToken, page, pageSize);
        Long count = UserService.getInstance().count(userToken);

        PaginationView paginationView = new PaginationView(page, pageSize, count);
        ListUserView listUserView = new ListUserView(users, paginationView);

        return ApiResponseController.ok(listUserView);
    }


    @PUT
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
            UserView userView,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        userView.checkIfValid();

        UserToken user = getUserToken(token);

        if(!user.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        boolean registration = AuthService.getInstance()
                .registration(
                        request, headers, uriInfo,
                        userView, null,
                        user.getIdUser()
                );

        if(!registration) {
            return ApiResponseController.error("Registration failed");
        }

        return ApiResponseController.ok();
    }


    @GET
    @Path("{idUser}")
    @Operation(summary = "Get a User by id", description = "Return a User as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User found",
                    content = { @Content(schema = @Schema(implementation = UserView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(
            @Parameter(name = "idUser", description = "User id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUser") String idUser,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idUser)) {
            return ApiResponseController.error("idUser is not valid");
        }

        UserView user = UserService.getInstance().getUserById(userToken, idUser);

        if(user == null) {
            return ApiResponseController.notFound("User not found");
        }

        return ApiResponseController.ok(user);
    }

    @POST
    @Path("{idUser}")
    @Operation(summary = "Update a user data instance", description = "Return a updated user instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = UserView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
            UserView userView,
            @Parameter(name = "idUser", description = "User id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUser") String idUser,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        userView.checkIfValid();

        UserService userService = UserService.getInstance();

        boolean updated = userService.updateUser(userToken, idUser, userView);
        if(!updated) {
            return ApiResponseController.error("Error during update user");
        }

        UserView user = userService.getUserById(userToken, idUser);

        if(user == null) {
            return ApiResponseController.error("Error during update user");
        }

        return ApiResponseController.ok(user);
    }

    @GET
    @Path("me")
    @Operation(summary = "Get a User by me", description = "Return a User as per me")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User found",
                    content = { @Content(schema = @Schema(implementation = UserView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserMe(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        UserView user = UserService.getInstance().getUserById(userToken, userToken.getIdUser());

        if(user == null) {
            return ApiResponseController.notFound("User not found");
        }

        return ApiResponseController.ok(user);
    }

    @POST
    @Path("me")
    @Operation(summary = "Update a my user data instance", description = "Return a updated user instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = UserMeView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserMe(
            UserMeView userView,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        userView.checkIfValid();

        UserService userService = UserService.getInstance();

        boolean updated = userService.updateUser(userToken, userToken.getIdUser(), userView);
        if(!updated) {
            return ApiResponseController.error("Error during update user");
        }

        UserMeView user = userService.getUserById(userToken, userToken.getIdUser());

        if(user == null) {
            return ApiResponseController.error("Error during update user");
        }

        return ApiResponseController.ok(user);
    }


    @DELETE
    @Path("{idUser}")
    @Operation(summary = "Remove a User instance identifier by parameter", description = "Delete a User instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(
            @Parameter(name = "idUser", description = "User id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUser") String idUser,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idUser)) {
            return ApiResponseController.error("idUser is not valid");
        }

        boolean deleted = UserService.getInstance().deleteUser(userToken, idUser);

        if(deleted) {
            return ApiResponseController.ok("User deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete user");
        }
    }


}
