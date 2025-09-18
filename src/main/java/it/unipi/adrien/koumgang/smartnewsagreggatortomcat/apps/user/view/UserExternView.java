package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class UserExternView extends BaseView {

    private String idUser;

    @Required
    private String firstName;

    @Required
    private String lastName;

    private String username;

    private String image;

    private String wallImage;

    public UserExternView() {}

    public UserExternView(User user) {
        this.idUser = StringIdConverter.getInstance().fromObjectId(user.getUserId());
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


    public String getIdUser() {
        return idUser;
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
