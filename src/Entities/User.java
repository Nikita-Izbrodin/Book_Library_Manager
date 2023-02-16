package Entities;

/**
 * An immutable data class for users.
 */
public record User(
        String username,
        String fullName,
        String password
) {}
