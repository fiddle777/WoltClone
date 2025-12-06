package com.example.woltbetblogiau.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Double price;
    @ManyToOne
    private BasicUser buyer;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Cuisine> cuisineList;
    @OneToOne
    private Chat chat;
    @ManyToOne
    private Restaurant restaurant;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;

    public FoodOrder(String name, Double price, BasicUser buyer, Restaurant restaurant) {
        this.name = name;
        this.price = price;
        this.buyer = buyer;
        this.restaurant = restaurant;
    }

    public FoodOrder(String name, Double price, BasicUser buyer, List<Cuisine> cuisineList, Restaurant restaurant) {
        this.name = name;
        this.price = price;
        this.buyer = buyer;
        this.cuisineList = cuisineList;
        this.restaurant = restaurant;
        this.status = OrderStatus.NEW;
    }

    @Override
    public String toString() {
        return name + " " + price;
    }
}