package com.campusbooking.repository;

import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User operations.
 * Concept: JPA Abstraction over relational database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(u.department) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchUsers(@Param("role") UserRole role, @Param("search") String search);

    long countByRole(UserRole role);
}
