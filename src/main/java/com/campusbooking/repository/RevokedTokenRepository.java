package com.campusbooking.repository;

import com.campusbooking.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByTokenHash(String tokenHash);

    List<RevokedToken> findByExpiryDateAfter(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RevokedToken r WHERE r.expiryDate < :cutoff")
    void deleteByExpiryDateBefore(@Param("cutoff") LocalDateTime cutoff);
}

