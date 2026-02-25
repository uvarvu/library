package com.aioannou.library.exception;

/**
 * Custom exception class.
 */
public class LibraryException extends Exception {

    private final LibraryErrors libraryError;

    public LibraryException(final LibraryErrors libraryError){
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
