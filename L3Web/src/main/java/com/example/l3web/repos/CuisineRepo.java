package com.example.l3web.repos;

import com.example.l3web.model.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuisineRepo extends JpaRepository<Cuisine, Integer> {
    List<Cuisine> getCuisineByRestaurantId(int id);
}
