package Entities;

/**
 * An immutable data class for members.
 */
public record Member(
        int id,
        String name,
        String surname,
        String phoneNo,
        String email,
        String address,
        String postcode
) {}
