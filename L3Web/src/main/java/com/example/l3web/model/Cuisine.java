package com.example.l3web.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cuisine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    protected String name;
    @Transient
    protected List<String> ingredients;
    protected Double price;
    protected boolean spicy = false;
    protected boolean vegan = false;
    @ManyToMany(mappedBy = "cuisineList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodOrder> orderList;

}
