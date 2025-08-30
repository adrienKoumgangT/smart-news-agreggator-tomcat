package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class ChangePasswordView extends BaseView {

    @Required
    private String oldPassword;

    @Required
    private String newPassword;

    public ChangePasswordView() {}

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
