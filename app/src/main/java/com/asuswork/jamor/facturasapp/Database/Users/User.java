package com.asuswork.jamor.facturasapp.Database.Users;

/**
 * Created by jamor on 28/02/2018.
 */

public class User {

    private String username;
    private String password;
    private boolean isPublic;

    public User(){}

    public User(String username, String password, String isPublic){
        this.username = username;
        this.password = password;
        this.isPublic = isPublic.toLowerCase().equals("y");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPublic(String isPublic){
        this.isPublic = isPublic.toLowerCase().equals("y");
    }

    public boolean isPublic(){
        return isPublic;
    }
}
