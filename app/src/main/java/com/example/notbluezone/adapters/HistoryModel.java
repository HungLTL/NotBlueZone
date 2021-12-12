package com.example.notbluezone.adapters;

public class HistoryModel {
    private String city, district, address, date;

    public HistoryModel(String city, String address, String date) {
        this.city = city;
        this.address = address;
        this.date = date;
    }

    public HistoryModel(String city, String district, String address, String date) {
        this.city = city;
        this.district = district;
        this.address = address;
        this.date = date;
    }

    public String getCity() { return city; }
    public String getDistrict() { if (district.length() > 0) return district; else return ""; }
    public String getAddress() { return address; }
    public String getVisitDate() { return date; }

    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setAddress(String address) { this.address = address; }
    public void setVisitDate(String date) { this.date = date; }
}
