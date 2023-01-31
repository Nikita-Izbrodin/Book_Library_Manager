package Entities;

public class User {

    private String username;
    private String fullName;
    private String password;

    public User(String username,String fullName, String password) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } // DO NOT NEED THIS

}
