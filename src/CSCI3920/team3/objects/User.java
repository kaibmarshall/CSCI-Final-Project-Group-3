package CSCI3920.team3.objects;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private ArrayList<Item> rentedItems;
    private String isAdmin;

    public User(String username, String password,  String isAdmin) {
        this.username = username;
        this.password = password;
        this.rentedItems = new ArrayList<>();
        this.isAdmin = isAdmin;
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

    public ArrayList<Item> getRentedItems() {
        return rentedItems;
    }

    public void setRentedItems(ArrayList<Item> rentedItems) {
        this.rentedItems = rentedItems;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(String admin) {
        isAdmin = admin;
    }
}
