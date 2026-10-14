package com.aioannou.library.exception;

import java.util.Objects;

/**
 * Custom exception class.
 */
public class LibraryException extends Exception {

    private final LibraryErrors libraryError;

    public LibraryException(final LibraryErrors libraryError){
        // Java 25 flexible constructor bodies allow validation before super(...).
        Objects.requireNonNull(libraryError, "libraryError must not be null");
        super(libraryError.getErrorMessage());
        this.libraryError = libraryError;
    }

    public int getErrorCode(){
        return libraryError.getErrorCode();
    }

    public LibraryErrors getLibraryError() {
        return libraryError;
    }
}
