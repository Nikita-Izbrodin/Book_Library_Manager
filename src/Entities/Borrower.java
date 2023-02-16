package Entities;

import java.time.LocalDate; // A date with the format (YYYY-MM-DD)

/**
 * An immutable data class for borrowers.
 */
public record Borrower(
        int bookID,
        int memberID,
        String fullName,
        LocalDate returnDate
) {}

