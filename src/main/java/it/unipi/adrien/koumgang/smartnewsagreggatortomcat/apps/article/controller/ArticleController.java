package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service.ArticleReaderService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service.ArticleService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleSimpleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ListArticleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.CommentResponse;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.ReplyResponse;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.service.CommentService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReactionsResponse;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.BaseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils.CollectionUtils;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.ListIdsView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/article")
@Tag(name = "Articles", description = "API operation related to articles")
public class ArticleController extends BaseController {

    @POST
    @Path("/upload-csv/{source}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadCsv(
            @Parameter(name = "source", description = "Source of csv article", example = "NYT")
            @PathParam("source") String source,
            @FormDataParam("file") InputStream fileStream,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }
        if (fileStream == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "No file uploaded under field 'file'"))
                    .build();
        }

        List<Article> articles = ArticleReaderService.readArticle(userToken, source, fileStream);

        Runnable task = () -> {
            try {
                long num = ArticleService.getInstance().loadArticles(userToken, articles);
                System.out.println(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        return ApiResponseController.ok();
    }


    @POST
    @Path("/{idArticle}/comment/upload-csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadCommentCsv(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @FormDataParam("file") InputStream fileStream,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }
        if (fileStream == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "No file uploaded under field 'file'"))
                    .build();
        }


        Runnable task = () -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        return ApiResponseController.ok();
    }


    @GET
    @Path("tags")
    @Operation(summary = "Get list of Tags", description = "Return a list of Tags present in articles")
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
    public Response getAllTags(@HeaderParam("Authorization") String token) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> tags = ArticleService.getInstance().listAllTags(userToken);

        return ApiResponseController.ok(tags);
    }

    @GET
    @Path("ids")
    @Operation(summary = "Get list of Id", description = "Return a list of Id present in articles")
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
    public Response getAllIds(@HeaderParam("Authorization") String token) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> ids = ArticleService.getInstance().listAllIds(userToken);

        return ApiResponseController.ok(CollectionUtils.shuffleStrings(ids));
    }

    @POST
    @Path("ids")
    @Operation(summary = "Get list of Id", description = "Return a list of Id present in articles")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIds(List<String> tags, @HeaderParam("Authorization") String token) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> ids;

        if(tags == null || tags.isEmpty()) ids = ArticleService.getInstance().listAllIds(userToken);
        else ids = ArticleService.getInstance().listIdsByTags(userToken, tags);

        return ApiResponseController.ok(CollectionUtils.shuffleStrings(ids));
    }

    @GET
    @Path("ids/list")
    @Operation(summary = "Get list of id of a Article instance", description = "Return a list of id of Article instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ListIdsView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIdsArticlePaginated(
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> ids = ArticleService.getInstance().listAllIds(userToken, page, pageSize != null ? pageSize : 100);
        Long count = ArticleService.getInstance().count(userToken);

        PaginationView paginationView = new PaginationView(page, pageSize, count);

        ListIdsView listIdsArticleView = new ListIdsView(CollectionUtils.shuffleStrings(ids), paginationView);

        return ApiResponseController.ok(listIdsArticleView);
    }

    @POST
    @Path("ids/list")
    @Operation(summary = "Get list of id of a Article instance", description = "Return a list of id of Article instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ListIdsView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIdsArticlePaginated(
            List<String> tags,
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> ids;
        if(tags == null || tags.isEmpty()) ids = ArticleService.getInstance().listAllIds(userToken, page, pageSize != null ? pageSize : 100);
        else ids = ArticleService.getInstance().listIdsByTags(userToken, tags, page, pageSize != null ? pageSize : 100);
        Long count = ArticleService.getInstance().count(userToken);

        PaginationView paginationView = new PaginationView(page, pageSize, count);

        ListIdsView listIdsArticleView = new ListIdsView(CollectionUtils.shuffleStrings(ids), paginationView);

        return ApiResponseController.ok(listIdsArticleView);
    }

    @GET
    @Path("ids/me")
    @Operation(summary = "Get list of id of a Article instance", description = "Return a list of id of Article instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ListIdsView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIdsArticlePaginatedByMe(
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        List<String> ids = ArticleService.getInstance().listAllIdsByMe(userToken, page, pageSize != null ? pageSize : 100);
        Long count = ArticleService.getInstance().count(userToken);

        PaginationView paginationView = new PaginationView(page, pageSize, count);

        ListIdsView listIdsArticleView = new ListIdsView(CollectionUtils.shuffleStrings(ids), paginationView);

        return ApiResponseController.ok(listIdsArticleView);
    }

    @GET
    @Path("list")
    @Operation(summary = "Get list of a Article instance", description = "Return a list of Article instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ListArticleView.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticlesPaginated(
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("1") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not an admin");
        }

        ArticleService articleService = ArticleService.getInstance();

        List<ArticleView> articleViews = articleService.listArticles(userToken, page, pageSize != null ? pageSize : 10);
        Long count = articleService.count(userToken);

        PaginationView paginationView = new PaginationView(page, pageSize, count);

        ListArticleView listArticleView = new ListArticleView(articleViews, paginationView);

        return ApiResponseController.ok(listArticleView);
    }


    @GET
    @Path("{idArticle}")
    @Operation(summary = "Get a Article by id", description = "Return a Article as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Article found",
                    content = { @Content(schema = @Schema(implementation = ArticleView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticle(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        ArticleView article = ArticleService.getInstance().getArticleById(userToken, idArticle);

        if(article == null) {
            return ApiResponseController.notFound("Article not found");
        }

        return ApiResponseController.ok(article);
    }


    @GET
    @Path("{idArticle}/simple")
    @Operation(summary = "Get a Article Simple Data by id", description = "Return a Article Simple Data as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Article found",
                    content = { @Content(schema = @Schema(implementation = ArticleSimpleView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleSimple(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);


        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        ArticleSimpleView article = ArticleService.getInstance().getArticleSimpleById(userToken, idArticle);

        if(article == null) {
            return ApiResponseController.notFound("Article not found");
        }

        return ApiResponseController.ok(article);
    }


    @PUT
    @Path("{idArticle}")
    @Operation(summary = "Update a Article instance", description = "Return a Article instance updated")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = ArticleView.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateArticle(
            ArticleView articleView,
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        articleView.checkIfValid();

        ArticleService articleService = ArticleService.getInstance();

        boolean updated = articleService.updateArticle(userToken, idArticle, articleView);
        if(!updated) {
            return ApiResponseController.error("Error during save article");
        }

        ArticleView article = articleService.getArticleById(userToken, idArticle);

        if(article == null) {
            return ApiResponseController.error("Error during save article");
        }

        return ApiResponseController.ok(article);
    }


    @POST
    @Operation(summary = "Create new Article instance", description = "Return a new Article instance")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = { @Content(schema = @Schema(implementation = ArticleView.class)) }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createArticle(
            ArticleView articleView,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        articleView.checkIfValid();

        ArticleView article = ArticleService.getInstance().saveArticle(userToken, articleView);

        if(article == null) {
            return ApiResponseController.error("Error during save article");
        }

        return ApiResponseController.ok(article);
    }


    @DELETE
    @Path("{idArticle}")
    @Operation(summary = "Remove a Article instance identifier by parameter", description = "Delete a Article instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteArticle(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        boolean deleted = ArticleService.getInstance().deleteArticle(userToken, idArticle);

        if(deleted) {
            return ApiResponseController.ok("Article deleted successfully");
        } else {
            return ApiResponseController.error("Error during delete article");
        }
    }


    // Comments


    @GET
    @Path("{idArticle}/comment")
    @Operation(summary = "Get a Article Comments by id", description = "Return a Article Comments as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Article found",
                    content = { @Content(schema = @Schema(implementation = CommentResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleComments(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        CommentResponse comments = ArticleService.getInstance().getComments(userToken, idArticle, 0, page, pageSize);

        return ApiResponseController.ok(comments);
    }

    @GET
    @Path("{idArticle}/comment/{idComment}/reply")
    @Operation(summary = "Get a Article Comment Replies by id", description = "Return a Article Comment Replies as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Article found",
                    content = { @Content(schema = @Schema(implementation = ReplyResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleCommentsReplies(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @Parameter(name = "idComment", description = "Comment id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idComment") String idComment,
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        ReplyResponse replies = ArticleService.getInstance().getReplies(userToken, idArticle, idComment, page, pageSize);

        return ApiResponseController.ok(replies);
    }



    @GET
    @Path("{idArticle}/reaction")
    @Operation(summary = "Get a Article Reactions by id", description = "Return a Article Reactions as per id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Article found",
                    content = { @Content(schema = @Schema(implementation = UserReactionsResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticleReactions(
            @Parameter(name = "idArticle", description = "Article id", example = "68b28e50c8c86a733de632d8")
            @PathParam("idArticle") String idArticle,
            @Parameter(description = "Page number (1-based), starting from 1", example = "1", required = true)
            @QueryParam("page") @DefaultValue("100") Integer page,
            @Parameter(description = "Page size (1-based), starting from 1", example = "10", required = false)
            @QueryParam("pageSize") @DefaultValue("1") Integer pageSize,
            @HeaderParam("Authorization") String token
    ) throws Exception {
        UserToken userToken = getUserToken(token);

        if(!userToken.isAdmin()) {
            return ApiResponseController.unauthorized("You are not admin");
        }

        if (!MongoAnnotationProcessor.isValidObjectId(idArticle)) {
            return ApiResponseController.error("idArticle is not valid");
        }

        UserReactionsResponse reactions = ArticleService.getInstance().getReactions(userToken, idArticle, page, pageSize);

        return ApiResponseController.ok(reactions);
    }

}
