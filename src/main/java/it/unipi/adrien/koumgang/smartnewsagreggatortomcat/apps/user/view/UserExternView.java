package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;

public class UserExternView {

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
