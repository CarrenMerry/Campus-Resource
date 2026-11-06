package com.campusbooking.service;

import com.campusbooking.dto.ResourceRequest;
import com.campusbooking.dto.ResourceResponseDto;
import com.campusbooking.entity.ResourceCategory;
import com.campusbooking.enums.ResourceStatus;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing campus facilities and equipment.
 * Concepts: Method Overloading, Collections, Streams.
 */
public interface ResourceService {

    // ==========================================
    // METHOD OVERLOADING (Resource Searches)
    // ==========================================

    /**
     * Overloaded: Finds all resources without filters.
     */
    List<ResourceResponseDto> findResources();

    /**
     * Overloaded: Finds resources matching a text search query.
     */
    List<ResourceResponseDto> findResources(String query);

    /**
     * Overloaded: Finds resources filtered by category and minimum capacity.
     */
    List<ResourceResponseDto> findResources(Long categoryId, Integer minCapacity);

    /**
     * Overloaded: Comprehensive multi-criteria filtering.
     */
    List<ResourceResponseDto> findResources(Long categoryId, Integer minCapacity, ResourceStatus status, String search);

    // ==========================================
    // CRUD AND LIFECYCLE MANAGEMENT
    // ==========================================

    ResourceResponseDto getResourceById(Long resourceId);

    ResourceResponseDto createResource(ResourceRequest request);

    ResourceResponseDto updateResource(Long resourceId, ResourceRequest request);

    void deleteResource(Long resourceId);

    ResourceResponseDto updateResourceStatus(Long resourceId, ResourceStatus status);

    // Categories
    List<ResourceCategory> getAllCategories();

    ResourceCategory createCategory(ResourceCategory category);

    // Collections usage: Map grouping resources by category
    Map<String, List<ResourceResponseDto>> getResourcesGroupedByCategory();
}
