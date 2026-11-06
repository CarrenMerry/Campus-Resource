package com.campusbooking.interfaces;

import com.campusbooking.enums.UserRole;

/**
 * Interface contract for system security principals.
 * Concept: Polymorphic abstraction for authenticating various system user types.
 */
public interface Authenticatable {

    /**
     * Unique identifier used for authentication (e.g. email address).
     */
    String getPrincipalIdentifier();

    /**
     * Hashed credential string for verification.
     */
    String getEncryptedCredential();

    /**
     * Role of the authenticated principal.
     */
    UserRole getRole();

    /**
     * Validates whether the account is active and allowed to log in.
     */
    boolean isAccountActive();
}
