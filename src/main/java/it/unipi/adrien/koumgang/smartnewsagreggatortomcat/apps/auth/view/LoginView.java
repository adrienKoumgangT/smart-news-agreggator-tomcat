package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class LoginView extends BaseView {

    @Required
    private String email;

    @Required
    private String password;

    public LoginView() {}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
