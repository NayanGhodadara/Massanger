package com.example.massanger.module;

import java.util.Date;

public class Data_user2 {
    public String name,img, senderid,receiverid,msg,id;
    public Date date;

    public Data_user2(String name, String img,String senderid,Date date,String msg,String receiverid,String id) {
        this.name = name;
        this.img = img;
        this.senderid = senderid;
        this.date = date;
        this.msg = msg;
        this.receiverid = receiverid;
        this.id = id;
    }
}
