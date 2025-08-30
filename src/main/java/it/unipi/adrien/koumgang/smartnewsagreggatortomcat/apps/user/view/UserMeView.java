package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.RegisterView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.AddressView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMeView extends UserExternView {

    private String email;

    private Date lastLoginAt;

    private List<AddressView> addresses;

    public UserMeView() {
        super();
    }

    public UserMeView(User user) {
        super(user);

        this.email = user.getEmail() != null ? user.getEmail().getEmail() : null;
        this.lastLoginAt = user.getLastLoginAt();
        this.addresses = user.getAddresses() != null ? user.getAddresses().stream().map(AddressView::new).toList() : new ArrayList<>();
    }

    public UserMeView(RegisterView registerView) {
        super(registerView);
        this.email = registerView.getEmail();
    }

    public String getEmail() {
        return email;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public List<AddressView> getAddresses() {
        return addresses;
    }
}
