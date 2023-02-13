package Entities;

import java.time.LocalDate; // A date with the format (YYYY-MM-DD)

public record Borrower(
        int bookID,
        int memberID,
        String fullName,
        LocalDate returnDate
) {}

