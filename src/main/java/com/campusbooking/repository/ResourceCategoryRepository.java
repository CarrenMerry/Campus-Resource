package com.campusbooking.repository;

import com.campusbooking.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Resource Categories.
 */
@Repository
public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {
    Optional<ResourceCategory> findByCategoryNameIgnoreCase(String categoryName);
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
