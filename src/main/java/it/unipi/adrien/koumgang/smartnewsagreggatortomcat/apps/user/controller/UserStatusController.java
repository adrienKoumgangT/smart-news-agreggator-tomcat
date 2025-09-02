package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserStatusView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/user/status")
@Tag(name = "Users Status", description = "API operation related to users status")
public class UserStatusController extends BaseController {

    @GET
    @Operation(summary = "Get list of a User Status instance", description = "Return list of User status instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserStatusView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsersStatus(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        List<UserStatusView> userStatusViews = UserService.getInstance().listUserStatus(userToken);

        return ApiResponseController.ok(userStatusViews);
    }

    @POST
    @Operation(summary = "Create new user status", description = "Create new user status in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserStatus(
            UserStatusView userStatusView,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        userStatusView.checkIfValid();

        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        UserStatusView userStatus = UserService.getInstance().createUserStatus(userToken, userStatusView);

        return ApiResponseController.ok(userStatus);
    }

    @GET
    @Path("{idUserStatus}")
    @Operation(summary = "Get a User Status by id", description = "Return a User Status as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User found",
                    content = { @Content(schema = @Schema(implementation = UserStatusView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserStatus(
            @Parameter(name = "idUserStatus", description = "User id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUserStatus") String idUserStatus,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idUserStatus)) {
            // return ApiResponseController.error("idUser is not valid");
        }

        UserStatusView userStatus = UserService.getInstance().getUserStatusById(userToken, idUserStatus);

        if(userStatus == null) {
            return ApiResponseController.notFound("User Status not found");
        }

        return ApiResponseController.ok(userStatus);
    }

    @PUT
    @Path("{idUserStatus}")
    @Operation(summary = "Update a user status data instance", description = "Return a updated user status instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = UserStatusView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserStatus(
            UserStatusView userStatusView,
            @Parameter(name = "idUserStatus", description = "User Status id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUserStatus") String idUserStatus,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        userStatusView.checkIfValid();

        UserService userService = UserService.getInstance();

        boolean updated = userService.updateUserStatus(userToken, idUserStatus, userStatusView);
        if(!updated) {
            return ApiResponseController.error("Error during update user status");
        }

        UserStatusView userStatus = userService.getUserStatusById(userToken, idUserStatus);

        if(userStatus == null) {
            return ApiResponseController.error("Error during update user status");
        }

        return ApiResponseController.ok(userStatus);
    }

    @DELETE
    @Path("{idUserStatus}")
    @Operation(summary = "Remove a User Status instance identifier by parameter", description = "Delete a User Status instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUserStatus(
            @Parameter(name = "idUserStatus", description = "User Status id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idUserStatus") String idUserStatus,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idUserStatus)) {
            // return ApiResponseController.error("idUser is not valid");
        }

        boolean deleted = UserService.getInstance().deleteUserStatus(userToken, idUserStatus);

        if(deleted) {
            return ApiResponseController.ok("User status deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete user status");
        }
    }

}
