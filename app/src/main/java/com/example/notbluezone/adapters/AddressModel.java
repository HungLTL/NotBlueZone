package com.example.notbluezone.adapters;

public class AddressModel {
    private String city, district;
    private String address, DateOfVisit;

    public AddressModel(String city, String address, String DateOfVisit) {
        this.city = city;
        this.district = "";
        this.address = address;
        this.DateOfVisit = DateOfVisit;
    }

    public AddressModel(String city, String district, String address, String DateOfVisit) {
        this.city = city;
        this.district = district;
        this.address = address;
        this.DateOfVisit = DateOfVisit;
    }

    public String getCityId() { return city; }
    public String getDistrictId() { return district; }
    public String getAddress() { return address; }
    public String getDateOfVisit() { return DateOfVisit; }
    public void setCityId(String id) { city = id; }
    public void setDistrictId(String id) { district = id; }
    public void setAddress(String address) { this.address = address; }
    public void setDateOfVisit(String DateOfVisit) { this.DateOfVisit = DateOfVisit; }
}
