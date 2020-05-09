package com.project.smartcartapp.Model;

public class ListProduct {
    private String pid, pname;

    public ListProduct(){

    }

    public ListProduct(String pid, String pname) {
        this.pid = pid;
        this.pname = pname;
    }

    public String getPid() {
        return pid;
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
