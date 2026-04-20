package com.ahesan.ecommerce.user_service.repository;

import com.ahesan.ecommerce.user_service.entity.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Address a SET a.defaultAddress = false WHERE a.user.id = :userId")
    void clearDefaultForUser(UUID userId);

    List<Address> findByUserId(UUID userId);

    Optional<Address> findTopByUserId(UUID userId);

    Optional<Address> findByIdAndUserId(UUID id, UUID userId);

}
