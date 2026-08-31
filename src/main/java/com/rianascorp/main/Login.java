package com.rianascorp.main;

public class Login {

    private String Usename;


    private String Passwd;



    public Login() {
    }

    public Login(String username, String password) {
        Usename = username;
        Passwd = password;
    }

    public String getUsername() {
        return Usename;
    }

    public void setUsername(String username) {
        Usename = username;
    }


    public String getPassword() {
        return Passwd;
    }

    public void setPassword(String password) {
        Passwd = password;
    }
}
