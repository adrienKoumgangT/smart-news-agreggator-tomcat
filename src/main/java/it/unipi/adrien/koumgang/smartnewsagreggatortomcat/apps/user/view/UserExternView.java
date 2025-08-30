package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserEmail;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserPassword;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

public class UserExternView extends BaseView {

    private String userId;

    private String firstName;

    private String lastName;

    private String username;

    private String image;

    private String wallImage;

    public UserExternView() {}

    public UserExternView(User user) {
        this.userId = user.getUserId() != null ? new StringIdConverter().fromObjectId(user.getUserId()) : null;

        this.firstName  = user.getFirstName();
        this.lastName   = user.getLastName();
        this.username   = user.getUsername();
        this.image      = user.getImage();
        this.wallImage  = user.getWallImage();
    }

    public UserExternView(RegisterView registerView) {
        this.username = registerView.getUsername();
        this.firstName = registerView.getFirstName();
        this.lastName = registerView.getLastName();
    }


    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public String getWallImage() {
        return wallImage;
    }
}
