package com.example.woltbetblogiau.consoleCRUD;

import com.example.woltbetblogiau.model.FoodOrder;
import com.example.woltbetblogiau.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Wolt implements Serializable {
    private List<User> allSystemUsers;
    private List<FoodOrder> allOrders;

    public Wolt() {
        this.allSystemUsers = new ArrayList<>();
        this.allOrders = new ArrayList<>();
    }

    public List<User> getAllSystemUsers() {
        return allSystemUsers;
    }

    public void setAllSystemUsers(List<User> allSystemUsers) {
        this.allSystemUsers = allSystemUsers;
    }

    public List<FoodOrder> getAllOrders() {
        return allOrders;
    }

    public void setAllOrders(List<FoodOrder> allOrders) {
        this.allOrders = allOrders;
    }
}
