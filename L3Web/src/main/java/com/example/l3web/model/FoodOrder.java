package com.example.l3web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    private String restaurantName = name;

    @Column(length = 1000)
    private String itemsSummary;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @JsonIgnore
    @ManyToOne
    private BasicUser buyer;

    @JsonIgnore
    @ManyToMany
    private List<Cuisine> cuisineList;

    @JsonIgnore
    @OneToOne
    private Chat chat;

    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        dateUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = LocalDateTime.now();
    }
}
