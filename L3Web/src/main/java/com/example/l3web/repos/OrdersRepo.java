package com.example.l3web.repos;

import com.example.l3web.model.FoodOrder;
import com.example.l3web.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepo extends JpaRepository<FoodOrder, Integer> {
    List<FoodOrder> getFoodOrdersByBuyer_Id(int id);
    List<FoodOrder> getFoodOrdersByDriver_Id(int id);
    List<FoodOrder> getFoodOrdersByDriverIsNullAndStatus(OrderStatus status);
}