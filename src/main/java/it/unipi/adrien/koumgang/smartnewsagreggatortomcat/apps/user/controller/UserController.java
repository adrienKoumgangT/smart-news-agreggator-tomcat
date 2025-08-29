package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.repository.UserRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.controller.ApiResponseController;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/user")
@Tag(name = "Users", description = "API operation related to users")
public class UserController {


    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {

        UserRepository userRepository = new UserRepository(MongoInstance.getInstance().mongoDatabase());

        UserService userService = new UserService(userRepository);

        List<User> users = userService.getUsersWithExpiredPasswords();
        List<UserView> userViews = users.stream().map(UserView::new).toList();

        return ApiResponseController.ok(userViews);
    }


}
