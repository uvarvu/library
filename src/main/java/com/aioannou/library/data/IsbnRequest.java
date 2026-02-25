package com.aioannou.library.data;

import jakarta.validation.constraints.NotBlank;

/**
 * Request object for ISBN-only operations.
 */
public record IsbnRequest(
    @NotBlank(message = "The ISBN must not be blank") String isbn
) {}
