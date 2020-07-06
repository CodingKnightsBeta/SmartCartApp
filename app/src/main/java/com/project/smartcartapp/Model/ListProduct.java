package com.project.smartcartapp.Model;

public class ListProduct {
    private String pid, pname, section;

    public ListProduct(){

    }

    public ListProduct(String pid, String pname, String section) {
        this.pid = pid;
        this.pname = pname;
        this.section = section;
    }

    public String getPid() {
        return pid;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
