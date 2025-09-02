package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service.UserStatus;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserMeView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordChecker;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordHasher;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.Address;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MongoCollectionName("users")
public class User extends BaseModel {

    @MongoId
    private ObjectId userId;

    @ModelField("firstname")
    private String firstName;

    @ModelField("lastname")
    private String lastName;

    @ModelField("username")
    // @MongoIndex(unique = true)
    private String username;

    @MongoEmbedded("email")
    private UserEmail email;

    @MongoEmbedded("password")
    private UserPassword password;

    @ModelField("is_admin")
    private Boolean isAdmin;

    @ModelField("status")
    private String status;

    @ModelField("image")
    private String image;

    @ModelField("wall_image")
    private String wallImage;

    @ModelField("last_login")
    @MongoDateTime(utc = true)
    private Date lastLoginAt;

    @MongoEmbeddedList("login_history")
    private List<LoginHistory> loginHistory;

    @MongoEmbeddedList("addresses")
    private List<Address> addresses;


    public User() {}

    public User(UserMeView user) {
        this.username   = user.getUsername();
        this.firstName  = user.getFirstName();
        this.lastName   = user.getLastName();
        this.email      = new UserEmail(user.getEmail());

        if(user.getAddresses() != null) {
            this.addresses = user.getAddresses().stream().map(Address::new).toList();
        } else this.addresses = new ArrayList<>();

        this.loginHistory = new ArrayList<>();
    }

    public void update(UserMeView user) {
        this.firstName  = user.getFirstName();
        this.lastName   = user.getLastName();

        if(user.getAddresses() != null) {
            this.addresses = user.getAddresses().stream().map(Address::new).toList();
        } else this.addresses = new ArrayList<>();
    }

    public void update(UserView user) {
        this.firstName  = user.getFirstName();
        this.lastName   = user.getLastName();
        this.isAdmin    = user.getIsAdmin();
        this.status     = user.getStatus();

        if(user.getAddresses() != null) {
            this.addresses = user.getAddresses().stream().map(Address::new).toList();
        } else this.addresses = new ArrayList<>();
    }

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

    public UserEmail getEmail() {
        return email;
    }

    public void setEmail(UserEmail email) {
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

    public boolean canLogin() {
        if(status == null) return false;

        UserStatus userStatus = UserStatus.fromCode(status);

        if(userStatus == null) return false;

        return switch (userStatus) {
            case UserStatus.ACTIVE,
                 UserStatus.PENDING,
                 UserStatus.VERIFIED,
                 UserStatus.GUEST -> true;
            default -> false;
        };
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

}
