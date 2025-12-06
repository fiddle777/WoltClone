package com.example.l3android.model;

public class FoodOrder {

    private int id;
    private String name;
    private Double price;

    private String restaurantName = name;
    private String itemsSummary;
    private String dateCreated;

    public FoodOrder(int id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public FoodOrder() {
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    // 🔹 new ones:

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
