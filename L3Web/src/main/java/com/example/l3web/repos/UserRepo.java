package com.example.l3web.repos;

import com.example.l3web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User getUserByLoginAndPassword(String login, String password);
    User getUserByLogin(String login);
}
