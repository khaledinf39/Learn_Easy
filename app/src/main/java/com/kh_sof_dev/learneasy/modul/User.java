package com.kh_sof_dev.learneasy.modul;

public class User {
    private String FullName,phone,uid;
    private int My_level=1;

    public User() {
    }

    public String getName() {
        return FullName;
    }

    public void setName(String name) {
        this.FullName = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLevel() {
        return My_level;
    }

    public void setLevel(int level) {
        this.My_level = level;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
