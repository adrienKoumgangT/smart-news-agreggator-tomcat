package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.Address;

public class AddressView {

    private String street;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private Boolean primary;

    public AddressView() {}

    public AddressView(Address address) {
        this.street     = address.getStreet();
        this.city       = address.getCity();
        this.state      = address.getState();
        this.zipCode    = address.getZipCode();
        this.country    = address.getCountry();
        this.primary    = address.isPrimary();
    }


    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getPrimary() {
        return primary;
    }

}