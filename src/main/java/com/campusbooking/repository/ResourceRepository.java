package com.campusbooking.repository;

import com.campusbooking.entity.Resource;
import com.campusbooking.enums.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Campus Resources.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByStatus(ResourceStatus status);

    List<Resource> findByCategory_CategoryId(Long categoryId);

    List<Resource> findByCategory_CategoryIdAndStatus(Long categoryId, ResourceStatus status);

    @Query("SELECT r FROM Resource r WHERE " +
           "(:categoryId IS NULL OR r.category.categoryId = :categoryId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:minCapacity IS NULL OR r.capacity >= :minCapacity) AND " +
           "(:search IS NULL OR LOWER(r.resourceName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Resource> filterResources(@Param("categoryId") Long categoryId,
                                   @Param("status") ResourceStatus status,
                                   @Param("minCapacity") Integer minCapacity,
                                   @Param("search") String search);

    long countByStatus(ResourceStatus status);
}
