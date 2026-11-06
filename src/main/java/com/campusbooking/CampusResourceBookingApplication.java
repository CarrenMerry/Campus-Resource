package com.campusbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Entry Point for the Campus Resource Booking System.
 * MCA Academic Project - Complete Full-Stack Solution.
 */
@SpringBootApplication
@EnableScheduling
public class CampusResourceBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusResourceBookingApplication.class, args);
    }
}
