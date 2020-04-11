package com.project.smartcartapp.Model;

public class Lists {
    private String lid, lname, description;

    public Lists(){

    }

    public Lists(String lid, String lname, String description) {
        this.lid = lid;
        this.lname = lname;
        this.description = description;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
