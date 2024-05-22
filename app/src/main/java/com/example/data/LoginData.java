package com.example.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginData implements Serializable {
    @SerializedName("login")
    public String login;
    @SerializedName("password")
    public String password;

    public LoginData(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
