package com.aioannou.library.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryExceptionTest {
    @Test
    void rejectsNullErrorBeforeSuperclassInitialization() {
        var exception = assertThrows(NullPointerException.class, () -> new LibraryException(null));
        assertEquals("libraryError must not be null", exception.getMessage());
    }

    @Test
    void retainsDomainErrorAndMessage() {
        var exception = new LibraryException(LibraryErrors.BOOK_LOANED_OUT);
        assertSame(LibraryErrors.BOOK_LOANED_OUT, exception.getLibraryError());
        assertEquals(LibraryErrors.BOOK_LOANED_OUT.getErrorMessage(), exception.getMessage());
        assertEquals(LibraryErrors.BOOK_LOANED_OUT.getErrorCode(), exception.getErrorCode());
    }
}
