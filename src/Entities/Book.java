package Entities;

/**
 * An immutable data class for books.
 */
public record Book(
        String title,
        String author,
        String isbn,
        int quantity
) {}
