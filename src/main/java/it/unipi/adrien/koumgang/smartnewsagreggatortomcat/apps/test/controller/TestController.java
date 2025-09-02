package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.service.TestService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.ListTestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/test")
@Tag(name = "Tests", description = "API operation related to test")
public class TestController extends BaseController {


    @GET
    @Operation(summary = "Get list of a Test instance", description = "Return a list of Test instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = TestView.class)) }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTests() throws Exception {

        List<TestView> testViews = TestService.getInstance().listTests();

        return ApiResponseController.ok(testViews);
    }

    @GET
    @Path("list")
    @Operation(summary = "Get list of a Test instance", description = "Return a list of Test instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    // content = @Content(array = @ArraySchema(schema = @Schema(implementation = TestView.class)))
                    content = @Content(schema = @Schema(implementation = ListTestView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTestsPaginated(
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("1") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize
    ) throws Exception {

        List<TestView> testViews = TestService.getInstance().listTests(page, pageSize != null ? pageSize : 10);
        Long count = TestService.getInstance().count();

        PaginationView paginationView = new PaginationView(page, pageSize, count);

        ListTestView listTestView = new ListTestView(testViews, paginationView);

        return ApiResponseController.ok(listTestView);
    }

    @GET
    @Path("secure")
    @Operation(summary = "Get list of a Test instance", description = "Return a list of Test instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TestView.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTestsWithToken(@HeaderParam("Authorization") String token) throws Exception {
        UserToken user = getUserToken(token);

        List<TestView> testViews = TestService.getInstance().listTests();

        return ApiResponseController.ok(testViews);
    }

    @GET
    @Path("{idTest}")
    @Operation(summary = "Get a Test by id", description = "Return a Test as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Test found",
                    content = { @Content(schema = @Schema(implementation = TestView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Test Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTest(
            @Parameter(name = "idTest", description = "Test id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idTest") String idTest
    ) throws Exception {

        if (!MongoAnnotationProcessor.isValidObjectId(idTest)) {
            return ApiResponseController.error("idTest is not valid");
        }

        TestView test = TestService.getInstance().getTestById(idTest);

        if(test == null) {
            return ApiResponseController.notFound("Test not found");
        }

        return ApiResponseController.ok(test);
    }

    @PUT
    @Path("{idTest}")
    @Operation(summary = "Update a Test instance", description = "Return a Test instance updated")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = TestView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTest(
            TestView testView,
            @Parameter(name = "idTest", description = "Test id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idTest") String idTest
    ) throws Exception {
        testView.checkIfValid();

        TestService testService = TestService.getInstance();

        boolean updated = testService.updateTest(idTest, testView);
        if(!updated) {
            return ApiResponseController.error("Error during save test");
        }

        TestView test = testService.getTestById(idTest);

        if(test == null) {
            return ApiResponseController.error("Error during save test");
        }

        return ApiResponseController.ok(test);
    }


    @POST
    @Operation(summary = "Create new Test instance", description = "Return a new Test instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = TestView.class)) }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTest(
            TestView testView
    ) throws Exception {
        testView.checkIfValid();

        TestView testViewNew = TestService.getInstance().saveTest(testView);

        if(testViewNew == null) {
            return ApiResponseController.error("Error during save test");
        }

        return ApiResponseController.ok(testViewNew);
    }


    @DELETE
    @Path("{idTest}")
    @Operation(summary = "Remove a Test instance identifier by parameter", description = "Delete a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTest(
            @Parameter(name = "idTest", description = "Test id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idTest") String idTest
    ) throws Exception {

        if (!MongoAnnotationProcessor.isValidObjectId(idTest)) {
            return ApiResponseController.error("idTest is not valid");
        }

        boolean deleted = TestService.getInstance().deleteTest(idTest);

        if(deleted) {
            return ApiResponseController.ok("Test deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete test");
        }
    }

}
