package CSCI3920.team3.objects;

import java.time.LocalDate;
import java.util.ArrayList;

public class Item {
    private String name;
    private String userRentedTo;
    private String pricePerDay;

    public Item(String name, String pricePerDay) {
        this.name = name;
        this.pricePerDay = pricePerDay;
    }

    public Item(String name, String userRentedTo, String pricePerDay) {
        this.name = name;
        this.userRentedTo = userRentedTo;
        this.pricePerDay = pricePerDay;
    }

    public String getUserRentedTo() {
        return userRentedTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", pricePerDay='" + pricePerDay + '\'' +
                ", userRenting=" + userRentedTo +
                '}';
    }
}
