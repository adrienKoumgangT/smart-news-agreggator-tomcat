package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserView extends UserMeView {

    private Boolean isAdmin;

    private String status;

    private List<LoginHistoryView> loginHistory;

    public UserView() {
        super();
    }

    public UserView(User user) {
        super(user);

        this.isAdmin = user.getAdmin();
        this.status = user.getStatus();

        this.loginHistory = user.getLoginHistory() != null ? user.getLoginHistory().stream().map(LoginHistoryView::new).toList() : new ArrayList<>();
    }

    public UserView(RegisterView registerView) {
        super(registerView);
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getStatus() {
        return status;
    }

    public List<LoginHistoryView> getLoginHistory() {
        return loginHistory;
    }
}
