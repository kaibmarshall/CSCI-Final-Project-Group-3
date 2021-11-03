package CSCI3920.team3.objects;

import java.time.LocalDate;
import java.util.ArrayList;

public class Item {
    private String name;
    private double pricePerDay;
    private ArrayList<LocalDate> datesRented;

    public Item(String name, double pricePerDay) {
        this.name = name;
        this.pricePerDay = pricePerDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public ArrayList<LocalDate> getDatesRented() {
        return datesRented;
    }

    public void addRentedDate(LocalDate date) {
        datesRented.add(date);
    }

    public void removeRentedDate(LocalDate date) {
        datesRented.remove(date);
    }

    public void clearAllRentedDated() {
        datesRented.clear();
    }

    public boolean isAvailable(LocalDate date) {
        return !datesRented.contains(date);
    }
}
