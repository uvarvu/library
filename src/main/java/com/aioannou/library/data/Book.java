package com.aioannou.library.data;

import java.io.Serializable;

/**
 * A Book object.
 */
public record Book(
    String isbn,
    String title,
    String author,
    int publicationYear,
    int availableCopies
) implements Serializable {}
