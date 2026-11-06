package com.campusbooking.interfaces;

/**
 * Interface representing campus entities whose operational lifecycle can be controlled by administrators.
 * Concept: Interface segregation for administrative management.
 */
public interface Manageable {

    /**
     * Activates the entity for standard campus operations.
     */
    void activate();

    /**
     * Deactivates or soft-deletes the entity.
     */
    void deactivate();

    /**
     * Places the entity into maintenance mode.
     */
    void setMaintenanceMode();

    /**
     * Queries whether the entity is in an active usable state.
     * @return boolean flag
     */
    boolean isActive();
}
