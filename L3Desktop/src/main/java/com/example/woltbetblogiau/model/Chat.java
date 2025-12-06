package com.example.woltbetblogiau.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String chatText;
    private LocalDate dateCreated;
    @OneToOne(mappedBy = "chat")
    private FoodOrder foodOrder;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> messages;

    public Chat(String name, FoodOrder foodOrder) {
        this.name = name;
        this.foodOrder = foodOrder;
        this.dateCreated = LocalDate.now();
        this.messages = new ArrayList<>();
    }
    @Override
    public String toString() {
        String orderPart = (foodOrder != null) ? ("Order #" + foodOrder.getId()) : "No order";
        return orderPart + " – " + (name != null ? name : "Chat");
    }
}