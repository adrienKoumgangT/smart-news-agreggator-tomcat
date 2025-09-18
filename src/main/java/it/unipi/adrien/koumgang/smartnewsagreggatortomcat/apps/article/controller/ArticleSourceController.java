package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service.ArticleSourceService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleSourceView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/article/source")
@Tag(name = "Articles Source", description = "API operation related to article source")
public class ArticleSourceController extends BaseController {

    @GET
    @Operation(
            summary = "Get list of a Article Sources instance",
            description = "Return list of Article Sources instance"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ArticleSourceView.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArticleSources(
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        List<ArticleSourceView> articleSourceViews = ArticleSourceService.getInstance().listArticleSources(userToken);

        return ApiResponseController.ok(articleSourceViews);
    }

    @POST
    @Operation(summary = "Create new article source", description = "Create new article source in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createArticleSource(
            ArticleSourceView articleSourceView,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        articleSourceView.checkIfValid();

        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        ArticleSourceView articleSource = ArticleSourceService.getInstance().createArticleSource(userToken, articleSourceView);

        return ApiResponseController.ok(articleSource);
    }

    @GET
    @Path("{idArticleSource}")
    @Operation(summary = "Get a Article Source by id", description = "Return a Article Source as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User found",
                    content = { @Content(schema = @Schema(implementation = ArticleSourceView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleSource(
            @Parameter(name = "idArticleSource", description = "Article Source id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticleSource") String idArticleSource,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticleSource)) {
            return ApiResponseController.error("idArticleSource is not valid");
        }

        ArticleSourceView articleSource = ArticleSourceService.getInstance().getArticleSourceById(userToken, idArticleSource);

        if(articleSource == null) {
            return ApiResponseController.notFound("Article Source not found");
        }

        return ApiResponseController.ok(articleSource);
    }

    @PUT
    @Path("{idArticleSource}")
    @Operation(summary = "Update a article source data instance", description = "Return a updated article source instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = ArticleSourceView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateArticleSource(
            ArticleSourceView articleSourceView,
            @Parameter(name = "idArticleSource", description = "Article source id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticleSource") String idArticleSource,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        articleSourceView.checkIfValid();

        ArticleSourceService articleSourceService = ArticleSourceService.getInstance();

        boolean updated = articleSourceService.updateArticleSource(userToken, idArticleSource, articleSourceView);
        if(!updated) {
            return ApiResponseController.error("Error during update article source");
        }

        ArticleSourceView articleSource = articleSourceService.getArticleSourceById(userToken, idArticleSource);

        if(articleSource == null) {
            return ApiResponseController.error("Error during update article source");
        }

        return ApiResponseController.ok(articleSource);
    }

    @DELETE
    @Path("{idArticleSource}")
    @Operation(summary = "Remove a Article Source instance identifier by parameter", description = "Delete a Article source instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteArticleSource(
            @Parameter(name = "idArticleSource", description = "Article Source id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticleSource") String idArticleSource,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticleSource)) {
            return ApiResponseController.error("idArticleSource is not valid");
        }

        boolean deleted = ArticleSourceService.getInstance().deleteArticleSource(userToken, idArticleSource);

        if(deleted) {
            return ApiResponseController.ok("Article Source deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete article source");
        }
    }

}
