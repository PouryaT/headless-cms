package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);

    Optional<User> findByEmail(String email);
}