package com.group4.fooddelivery_merchantapp.model;
import android.location.Address;
import android.net.Uri;

import java.util.ArrayList;

public class Merchant {
    private String Id;
    private String Name;
    private android.location.Address Address;
    private String Email;
    private String Phone;
    private ArrayList<Uri> Image = new ArrayList<Uri>();

    public Merchant() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public android.location.Address getAddress() {
        return Address;
    }

    public void setAddress(android.location.Address address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public ArrayList<Uri> getImage() {
        return Image;
    }

    public void setImage(ArrayList<Uri> image) {
        Image = image;
    }
}
