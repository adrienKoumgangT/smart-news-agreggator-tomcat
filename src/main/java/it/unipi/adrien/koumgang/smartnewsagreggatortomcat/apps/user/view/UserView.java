package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserView extends UserMeView {

    private Boolean isAdmin;

    private String status;

    private Date createdAt;

    private Date updatedAt;

    private List<LoginHistoryView> loginHistory;

    public UserView() {
        super();
    }

    public UserView(User user) {
        super(user);

        this.isAdmin = user.getAdmin();
        this.status = user.getStatus();

        this.createdAt  = user.getCreatedAt();
        this.updatedAt  = user.getUpdatedAt();

        this.loginHistory = user.getLoginHistory() != null ? user.getLoginHistory().stream().map(LoginHistoryView::new).toList() : new ArrayList<>();
    }

    public UserView(RegisterView registerView) {
        super(registerView);
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public List<LoginHistoryView> getLoginHistory() {
        return loginHistory;
    }
}
