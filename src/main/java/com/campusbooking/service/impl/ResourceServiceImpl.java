package com.campusbooking.service.impl;

import com.campusbooking.dto.ResourceRequest;
import com.campusbooking.dto.ResourceResponseDto;
import com.campusbooking.entity.Resource;
import com.campusbooking.entity.ResourceCategory;
import com.campusbooking.enums.ResourceStatus;
import com.campusbooking.exception.ResourceNotFoundException;
import com.campusbooking.repository.ResourceCategoryRepository;
import com.campusbooking.repository.ResourceRepository;
import com.campusbooking.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ResourceService.
 * 
 * Concepts Demonstrated:
 * - Method Overloading: 4 overloaded findResources() methods.
 * - Streams & Lambdas: Grouping, sorting, filtering collections.
 * - Collections Framework: List, Map, HashMap, ArrayList.
 * - Exception Handling: ResourceNotFoundException.
 * - Layered Architecture: Decoupled service layer.
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceCategoryRepository categoryRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository, ResourceCategoryRepository categoryRepository) {
        this.resourceRepository = resourceRepository;
        this.categoryRepository = categoryRepository;
    }

    // ==========================================
    // METHOD OVERLOADING DEMONSTRATIONS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponseDto> findResources() {
        return findResources(null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponseDto> findResources(String query) {
        return findResources(null, null, null, query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponseDto> findResources(Long categoryId, Integer minCapacity) {
        return findResources(categoryId, minCapacity, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponseDto> findResources(Long categoryId, Integer minCapacity, ResourceStatus status, String search) {
        // Concept: Streams & Lambdas used for processing query results
        String sanitizedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<Resource> resources = resourceRepository.filterResources(categoryId, status, minCapacity, sanitizedSearch);

        return resources.stream()
                .map(ResourceResponseDto::fromEntity)
                .sorted(Comparator.comparing(ResourceResponseDto::getResourceName))
                .collect(Collectors.toList());
    }

    // ==========================================
    // COLLECTIONS FRAMEWORK DEMONSTRATION
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ResourceResponseDto>> getResourcesGroupedByCategory() {
        List<Resource> resources = resourceRepository.findAll();

        // Concept: Java Streams collectors groupingBy to create Map<String, List<ResourceResponseDto>>
        return resources.stream()
                .map(ResourceResponseDto::fromEntity)
                .collect(Collectors.groupingBy(
                        dto -> dto.getCategoryName() != null ? dto.getCategoryName() : "General",
                        HashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));
    }

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public ResourceResponseDto getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
        return ResourceResponseDto.fromEntity(resource);
    }

    @Override
    @Transactional
    public ResourceResponseDto createResource(ResourceRequest request) {
        ResourceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Resource resource = new Resource(
                request.getResourceName().trim(),
                category,
                request.getLocation().trim(),
                request.getCapacity(),
                request.getDescription(),
                request.getFacilities(),
                request.getImageUrl()
        );

        if (request.getStatus() != null) {
            resource.setStatus(request.getStatus());
        }

        Resource saved = resourceRepository.save(resource);
        return ResourceResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public ResourceResponseDto updateResource(Long resourceId, ResourceRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        ResourceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        resource.setResourceName(request.getResourceName().trim());
        resource.setCategory(category);
        resource.setLocation(request.getLocation().trim());
        resource.setCapacity(request.getCapacity());
        resource.setDescription(request.getDescription());
        resource.setFacilities(request.getFacilities());
        if (request.getStatus() != null) {
            resource.setStatus(request.getStatus());
        }
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            resource.setImageUrl(request.getImageUrl().trim());
        }

        Resource updated = resourceRepository.save(resource);
        return ResourceResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        // Soft deletion preferred as per project specs: set status to INACTIVE to preserve historical bookings
        resource.deactivate();
        resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public ResourceResponseDto updateResourceStatus(Long resourceId, ResourceStatus status) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        resource.setStatus(status);
        Resource updated = resourceRepository.save(resource);
        return ResourceResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public ResourceCategory createCategory(ResourceCategory category) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(category.getCategoryName())) {
            throw new IllegalArgumentException("Category with name '" + category.getCategoryName() + "' already exists.");
        }
        return categoryRepository.save(category);
    }
}
