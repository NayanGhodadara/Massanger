package com.example.massanger.module;


import java.util.Date;

public class Data_ViewType {

   public String viewType,txtName,img;
   public Date dateObj;


    public Data_ViewType(String viewType, String txtName, String img,Date dateObj) {
        this.viewType = viewType;
        this.txtName = txtName;
        this.img = img;
        this.dateObj = dateObj;
    }
    public Data_ViewType(String viewType, String txtName,Date dateObj) {
        this.viewType = viewType;
        this.txtName = txtName;
        this.img = img;
        this.dateObj = dateObj;
    }
}
