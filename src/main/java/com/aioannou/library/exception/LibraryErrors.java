package com.aioannou.library.exception;

/**
 * Enum of error codes.
 */
public enum LibraryErrors {
    BOOK_ALREADY_EXISTS(1, "This book already exists in the database"),
    BOOK_DOES_NOT_EXIST(2, "This book does not exist in the database"),
    BOOK_LOANED_OUT(3, "This book has no more available copies"),
    BOOK_UNKNOWN(4, "This book is not from the library"),
    AUTHOR_UNKNOWN(5, "No books from author found in the library");

    private final int errorCode;

    private final String errorMessage;

    LibraryErrors(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}