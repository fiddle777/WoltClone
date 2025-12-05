package com.example.l3web.repos;

import com.example.l3web.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<Chat, Integer> {
    Chat getChatByFoodOrder_Id(int id);
}
