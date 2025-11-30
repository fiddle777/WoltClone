package com.example.l3web.repos;

import com.example.l3web.model.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicUserRepo extends JpaRepository<BasicUser, Integer> {
}
