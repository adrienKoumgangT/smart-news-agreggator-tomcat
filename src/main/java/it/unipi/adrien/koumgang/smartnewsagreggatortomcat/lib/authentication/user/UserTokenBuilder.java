package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;

public class UserTokenBuilder {

    private String idUser;
    private String username;
    private String firstname;
    private String lastname;
    private Boolean isAdmin;
    private String status;

    public UserTokenBuilder() {}

    public UserTokenBuilder(User user) {
        this.idUser     = new StringIdConverter().fromObjectId(user.getUserId());
        this.username   = user.getUsername();
        this.firstname  = user.getFirstName();
        this.lastname   = user.getLastName();
        this.isAdmin    = user.getAdmin();
        this.status     = user.getStatus();
    }

    public UserTokenBuilder setIdUser(String idUser) {
        this.idUser = idUser;
        return this;
    }

    public UserTokenBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTokenBuilder setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public UserTokenBuilder setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public UserTokenBuilder setAdmin(Boolean admin) {
        isAdmin = admin;
        return this;
    }

    public UserTokenBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public UserToken build() {
        return new UserToken(
                this.idUser,
                this.username,
                this.firstname,
                this.lastname,
                this.isAdmin,
                this.status
        );
    }

}
