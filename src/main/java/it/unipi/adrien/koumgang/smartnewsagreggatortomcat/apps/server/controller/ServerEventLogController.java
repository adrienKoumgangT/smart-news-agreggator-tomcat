package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.service.ServerEventLogService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view.EventNamesView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view.ServerEventLogView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/server/event/log")
@Tag(name = "Server Error Log", description = "API operation related to server event log")
public class ServerEventLogController extends BaseController {

    @GET
    @Operation(summary = "Get list of a Server Event Log instance", description = "Get a Server Event Log instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServerEventLogView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServerEventLogs(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        List<ServerEventLogView> serverEventLogViews = ServerEventLogService.getInstance().listServerEventLogs(userToken);

        return ApiResponseController.ok(serverEventLogViews);
    }

    @GET
    @Path("event")
    @Operation(summary = "Get list of a Server Event Log Event Name", description = "Return a list of Server Event Log Name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServerEventLogEvents(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        List<String> events = ServerEventLogService.getInstance().listDistinctEvents(userToken);

        return ApiResponseController.ok(events);
    }

    @GET
    @Path("event/name")
    @Operation(summary = "Get list of a Server Event Log Event Name", description = "Return a list of Server Event Log Name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation (Map of event to list of names)",
                    content = @Content(array = @ArraySchema(schema =  @Schema(implementation = EventNamesView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServerEventLogEventsNames(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        Map<String, List<String>> eventsNames = ServerEventLogService.getInstance().getDistinctEventsNames(userToken);

        List<EventNamesView> result = EventNamesView.fromMap(eventsNames);

        return ApiResponseController.ok(result);
    }

    @GET
    @Path("by/event/{event}")
    @Operation(summary = "Get list of a Server Event Log instance by event", description = "Get a Server Event Log instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServerEventLogView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServerEventLogsByEvent(
            @PathParam("event") String event,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        List<ServerEventLogView> serverEventLogViews = event.isBlank()
                ? ServerEventLogService.getInstance().listServerEventLogs(userToken)
                : ServerEventLogService.getInstance().listServerEventLogsByEvent(userToken, event)
                ;

        return ApiResponseController.ok(serverEventLogViews);
    }

    @GET
    @Path("by/name/{name}")
    @Operation(summary = "Get list of a Server Event Log instance by event", description = "Get a Server Event Log instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServerEventLogView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllServerEventLogsByName(
            @PathParam("name") String name,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        List<ServerEventLogView> serverEventLogViews = name.isBlank()
                ? ServerEventLogService.getInstance().listServerEventLogs(userToken)
                : ServerEventLogService.getInstance().listServerEventLogsByName(userToken, name)
                ;

        return ApiResponseController.ok(serverEventLogViews);
    }

    @GET
    @Path("{idServerEventLog}")
    @Operation(summary = "Get a Server Event Log instance by event", description = "Get a Server Event Log instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ServerEventLogView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServerEventLog(
            @PathParam("idServerEventLog") String idServerEventLog,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idServerEventLog)) {
            return ApiResponseController.error("idTest is not valid");
        }

        Optional<ServerEventLogView> optionalServerEventLogView = ServerEventLogService.getInstance().getServerEventLog(userToken, idServerEventLog);

        if(optionalServerEventLogView.isPresent()) {
            return ApiResponseController.ok(optionalServerEventLogView.get());
        }

        return ApiResponseController.notFound("Test not found");
    }

    @DELETE
    @Path("{idServerEventLog}")
    @Operation(summary = "Remove a Server Event Log instance identifier by parameter", description = "Delete a Server Event Log instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteServerEventLog(
            @PathParam("idServerEventLog") String idServerEventLog,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idServerEventLog)) {
            return ApiResponseController.error("idTest is not valid");
        }

        boolean deleted = ServerEventLogService.getInstance().deleteServerEventLog(userToken, idServerEventLog);

        if(deleted) {
            return ApiResponseController.ok("Server Event Log deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete server event log");
        }
    }

}
