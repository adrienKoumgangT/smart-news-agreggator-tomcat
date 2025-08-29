package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.service.TestService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/test")
@Tag(name = "Tests", description = "API operation related to test")
public class TestController extends BaseController {


    @GET
    @Operation(summary = "Get list of a Test instance", description = "Get a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
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
    @Path("secure")
    @Operation(summary = "Get list of a Test instance", description = "Get a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
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
    @Operation(summary = "Get a Test instance", description = "Get a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTest(@PathParam("idTest") String idTest) throws Exception {

        if (!MongoAnnotationProcessor.isValidObjectId(idTest)) {
            return ApiResponseController.error("idTest is not valid");
        }

        Optional<TestView> optionalTest = TestService.getInstance().getTestById(idTest);

        if(optionalTest.isPresent()) {
            return ApiResponseController.ok(optionalTest.get());
        }

        return ApiResponseController.notFound("Test not found");
    }

    @POST
    @Path("{idTest}")
    @Operation(summary = "Post a Test instance", description = "Add a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postTest(TestView testView, @PathParam("idTest") String idTest) throws Exception {

        TestService testService = TestService.getInstance();

        boolean updated = testService.updateTest(idTest, testView);
        if(!updated) {
            return ApiResponseController.error("Error during save test");
        }

        Optional<TestView> optionalTest = TestService.getInstance().getTestById(idTest);

        if(optionalTest.isPresent()) {
            return ApiResponseController.ok(optionalTest.get());
        }

        return ApiResponseController.notFound("Test not found");
    }


    @PUT
    @Operation(summary = "Put a Test instance", description = "Update a Test instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putTest(TestView testView) throws Exception {

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
    public Response deleteTest(@PathParam("idTest") String idTest) throws Exception {

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
