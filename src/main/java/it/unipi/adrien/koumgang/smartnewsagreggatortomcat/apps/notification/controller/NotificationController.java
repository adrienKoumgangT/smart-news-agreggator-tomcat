package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/notification")
@Tag(name = "Notifications", description = "API operation related to notifications")
public class NotificationController extends BaseController {

    @GET
    @Operation(summary = "Get list of a Notification instance", description = "Return a list of Notification instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation"
                    // content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@HeaderParam("Authorization") String token) throws Exception {
        UserToken user = getUserToken(token);


        return ApiResponseController.ok(new ArrayList<>());
    }

    @GET
    @Path("latest")
    @Operation(summary = "Get list of a Notification instance", description = "Return a list of Notification instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation"
                    // content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestNotification(@HeaderParam("Authorization") String token) throws Exception {
        UserToken user = getUserToken(token);


        return ApiResponseController.ok(new ArrayList<>());
    }

}
