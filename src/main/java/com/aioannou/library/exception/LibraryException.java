package com.aioannou.library.exception;

/**
 * Custom exception class.
 */
public class LibraryException extends Exception {

    private int errorCode;

    public LibraryException(){
        // Default Constructor;
    }
    public LibraryException (final LibraryErrors libraryError){
        super(libraryError.getErrorMessage());
        this.errorCode = libraryError.getErrorCode();
    }

    public int getErrorCode(){
        return errorCode;
    }
}