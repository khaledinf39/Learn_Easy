package com.kh_sof_dev.learneasy.modul;

public class User {
    private String FullName,phone,uid;
    private int My_level=1;

    public User() {
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getMy_level() {
        return My_level;
    }

    public void setMy_level(int my_level) {
        My_level = my_level;
    }
}
