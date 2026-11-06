package com.campusbooking.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interface contract defining bookable behaviors for campus resources.
 * Concept: Abstraction and Interface programming - allows uniform booking operations.
 */
public interface Bookable {
    
    /**
     * Verifies if this resource is available for the given date and time window.
     * @param date target date
     * @param startTime requested start time
     * @param endTime requested end time
     * @return true if available without conflicts
     */
    boolean checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * Validates that requested capacity fits within resource bounds.
     * @param requestedCapacity number of attendees
     * @return true if within capacity
     */
    boolean validateCapacity(int requestedCapacity);

    /**
     * Checks if the resource is currently open and bookable.
     * @return boolean flag
     */
    boolean isBookableNow();
}
