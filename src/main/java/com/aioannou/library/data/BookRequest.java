package com.aioannou.library.data;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.Year;

/**
 * Book Request object used for validation of incoming attributes.
 */
public record BookRequest(
    @NotBlank(message = "The ISBN must not be blank") String isbn,
    @NotBlank(message = "The title must not be blank") String title,
    @NotBlank(message = "The author must not be blank") String author,
    @Min(value = 1500, message = "The year must be valid") int publicationYear,
    @Min(value = 1, message = "There must be one or more available copies") int availableCopies
) {
    @AssertTrue(message = "The year must be valid")
    public boolean isPublicationYearValid() {
        return publicationYear <= Year.now().getValue();
    }
}
