package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class RegisterView extends BaseView {

    @Required
    private String username;

    @Required
    private String firstName;

    @Required
    private String lastName;

    private String email;

    @Required
    private String password;


    public RegisterView() {}


    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
