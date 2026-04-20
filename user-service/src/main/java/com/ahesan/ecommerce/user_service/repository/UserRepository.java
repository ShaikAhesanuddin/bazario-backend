package com.ahesan.ecommerce.user_service.repository;

import com.ahesan.ecommerce.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

}
