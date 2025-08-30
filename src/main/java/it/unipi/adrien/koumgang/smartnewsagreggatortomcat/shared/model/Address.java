package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.AddressView;

public class Address {

    @ModelField("street")
    private String street;

    @ModelField("city")
    private String city;

    @ModelField("state")
    private String state;

    @ModelField("zip_code")
    private String zipCode;

    @ModelField("country")
    private String country;

    @ModelField("is_primary")
    private Boolean primary;

    public Address() {}

    public Address(String street, String city, String state, String zipCode, String country) {
        this.street     = street;
        this.city       = city;
        this.state      = state;
        this.zipCode    = zipCode;
        this.country    = country;
    }

    public Address(AddressView addressView) {
        this.street     = addressView.getStreet();
        this.city       = addressView.getCity();
        this.state      = addressView.getState();
        this.zipCode    = addressView.getZipCode();
        this.country    = addressView.getCountry();
    }

    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Boolean isPrimary() { return primary; }
    public void setPrimary(Boolean primary) { this.primary = primary; }
}
