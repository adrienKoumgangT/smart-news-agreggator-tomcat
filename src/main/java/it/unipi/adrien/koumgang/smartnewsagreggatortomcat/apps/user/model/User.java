package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordChecker;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordHasher;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoBaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.Address;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.LoginHistory;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MongoCollectionName("users")
public class User extends MongoBaseModel {

    @MongoId
    private ObjectId userId;

    @MongoField("firstname")
    private String firstName;

    @MongoField("lastname")
    private String lastName;

    @MongoField("username")
    // @MongoIndex(unique = true)
    private String username;

    @MongoField("email")
    // @MongoIndex(unique = true)
    private String email;

    @MongoEmbedded("password")
    private UserPassword password;

    @MongoField("is_admin")
    private Boolean isAdmin;

    @MongoField("status")
    private String status;

    @MongoField("image")
    private String image;

    @MongoField("wall_image")
    private String wallImage;

    @MongoField("last_login")
    @MongoDateTime(utc = true)
    private Date lastLoginAt;

    @MongoEmbeddedList("login_history")
    private List<LoginHistory> loginHistory;

    @MongoEmbeddedList("addresses")
    private List<Address> addresses;


    public User() {}

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserPassword getPassword() {
        return password;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWallImage() {
        return wallImage;
    }

    public void setWallImage(String wallImage) {
        this.wallImage = wallImage;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public List<LoginHistory> getLoginHistory() {
        return loginHistory;
    }


    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    // Business methods

    public void setPassword(String plainPassword) {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        this.password = new UserPassword(hashedPassword);
    }

    public boolean verifyPassword(String plainPassword) {
        if (this.password == null || this.password.isLocked()) {
            return false;
        }

        boolean matches = PasswordChecker.checkPassword(this.password.getPasswordHash(), plainPassword);

        if (matches) {
            this.password.resetFailedAttempts();
        } else {
            this.password.recordFailedAttempt();
        }

        return matches;
    }

    public void recordLogin() {
        this.lastLoginAt = new Date();

        // Add to login history
        if (this.loginHistory == null) {
            this.loginHistory = new ArrayList<>();
        }
        this.loginHistory.add(new LoginHistory(this.lastLoginAt, "success"));
    }

    public void recordFailedLogin() {
        this.lastLoginAt = new Date();

        // Add to login history
        if (this.loginHistory == null) {
            this.loginHistory = new ArrayList<>();
        }
        this.loginHistory.add(new LoginHistory(this.lastLoginAt, "failed"));
    }
}
