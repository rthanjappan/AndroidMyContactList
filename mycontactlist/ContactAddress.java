package com.example.mycontactlist;

public class ContactAddress {

    private int contactID;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    public ContactAddress(int contactID) {
        this.contactID = contactID;
    }

    public ContactAddress(int contactID,
                          String streetAddress,
                          String city,
                          String state,
                          String zipCode) {
        this.contactID = contactID;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
