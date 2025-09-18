package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user;

import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils.ToString;

import java.util.HashMap;
import java.util.Map;

public class UserToken {

    private String idUser;

    private String username;
    private String firstname;
    private String lastname;
    private Boolean isAdmin;
    private String status;


    public UserToken() {}

    public UserToken(User user) {
        this.idUser = new StringIdConverter().fromObjectId(user.getUserId());

        this.username   = user.getUsername();
        this.firstname  = user.getFirstName();
        this.lastname   = user.getLastName();
        this.isAdmin    = user.getAdmin();
        this.status     = user.getStatus();
    }

    public UserToken(
            String idUser,
            String username,
            String firstname,
            String lastname,
            Boolean isAdmin,
            String status
    ) {
        this.idUser     = idUser;
        this.username   = username;
        this.firstname  = firstname;
        this.lastname   = lastname;
        this.isAdmin    = isAdmin;
        this.status     = status;
    }


    public String getIdUser() {
        return this.idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Boolean isAdmin() {
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

    @Override
    public String toString() {
        try {
            return new GsonBuilder().serializeNulls().create().toJson(this);
        } catch (Exception e) {
            // e.printStackTrace();

            return ToString.builder("User")
                    .add("idUser", idUser)
                    .add("username", username)
                    .add("firstname", firstname)
                    .add("lastname", lastname)
                    .add("isAdmin", isAdmin)
                    .add("status", status)
                    .build();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("idUser", idUser);
        map.put("username", username);
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        map.put("isAdmin", isAdmin);
        map.put("status", status);
        return map;
    }

    public static UserToken fromMap(Map<String, Object> map) {
        UserToken authentification = new UserToken();
        authentification.setIdUser((String) map.get("idUser"));
        authentification.setUsername((String) map.get("username"));
        authentification.setFirstname((String) map.get("firstname"));
        authentification.setLastname((String) map.get("lastname"));
        authentification.setAdmin((Boolean) map.get("isAdmin"));
        authentification.setStatus((String) map.get("status"));
        return authentification;
    }

    public static void main(String[] args) {
        UserToken user = new UserToken();
        user.setIdUser("1234567890");
        user.setUsername("adrientkoumgang@gmail.com");
        user.setFirstname("adrien");
        user.setLastname("koumgang tegantchouang");
        user.setAdmin(true);
        user.setStatus("active");
        System.out.println(user);
    }

}