package com.example.l3web.repos;

import com.example.l3web.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {
}
