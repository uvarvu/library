package com.aioannou.library.data;

import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    private Library library;
    private static final Book BOOK = new Book("978-0241578186", "The Twits", "Roald Dahl", 2022, 10);

    @BeforeEach
    void setUp() throws LibraryException {
        library = new Library();
        library.addBook(BOOK);
    }

    @Test
    void singletonStateIsScopedToEachApplicationContext() throws LibraryException {
        try (var first = new org.springframework.context.annotation.AnnotationConfigApplicationContext(Library.class);
             var second = new org.springframework.context.annotation.AnnotationConfigApplicationContext(Library.class)) {
            Library firstLibrary = first.getBean(Library.class);
            assertSame(firstLibrary, first.getBean(Library.class));
            assertNotSame(firstLibrary, second.getBean(Library.class));
            firstLibrary.addBook(BOOK);
            assertError(LibraryErrors.BOOK_DOES_NOT_EXIST,
                () -> second.getBean(Library.class).findBookByISBN(BOOK.isbn()));
        }
    }

    @Test
    void addAndFindBook() throws LibraryException {
        Book another = new Book("978-0141318202", "Magnus Powermouse", "Dick King Smith", 2005, 2);
        library.addBook(another);
        assertEquals(another, library.findBookByISBN(another.isbn()));
    }

    @Test
    void duplicateAddPreservesOriginalBook() {
        assertError(LibraryErrors.BOOK_ALREADY_EXISTS, () -> library.addBook(BOOK));
    }

    @Test
    void removeBook() throws LibraryException {
        library.removeBook(BOOK);
        assertError(LibraryErrors.BOOK_DOES_NOT_EXIST, () -> library.findBookByISBN(BOOK.isbn()));
        assertError(LibraryErrors.BOOK_DOES_NOT_EXIST, () -> library.removeBook(BOOK));
    }

    @Test
    void authorResultsAreSortedAndImmutable() throws LibraryException {
        Book another = new Book("123", "Another", BOOK.author(), 2020, 1);
        library.addBook(another);
        List<Book> books = library.findBooksByAuthor(BOOK.author());
        assertEquals(List.of(another, BOOK), books);
        assertThrows(UnsupportedOperationException.class, () -> books.clear());
        assertError(LibraryErrors.AUTHOR_UNKNOWN, () -> library.findBooksByAuthor("Unknown"));
    }

    @Test
    void borrowAndReturnLeaveOriginalSnapshotUnchanged() throws LibraryException {
        library.borrowBook(BOOK.isbn());
        assertEquals(9, library.findBookByISBN(BOOK.isbn()).availableCopies());
        assertEquals(10, BOOK.availableCopies());
        library.returnBook(BOOK.isbn());
        assertEquals(BOOK, library.findBookByISBN(BOOK.isbn()));
    }

    @Test
    void missingBookCannotBeBorrowedOrReturned() {
        assertError(LibraryErrors.BOOK_DOES_NOT_EXIST, () -> library.borrowBook("unknown"));
        assertError(LibraryErrors.BOOK_UNKNOWN, () -> library.returnBook("unknown"));
    }

    @Test
    @Timeout(10)
    void concurrentBorrowersCannotOverdrawAvailableCopies() throws Exception {
        List<Boolean> results = concurrently(100, () -> {
            try {
                library.borrowBook(BOOK.isbn());
                return true;
            } catch (LibraryException e) {
                assertEquals(LibraryErrors.BOOK_LOANED_OUT, e.getLibraryError());
                return false;
            }
        });
        assertEquals(10, results.stream().filter(Boolean::booleanValue).count());
        assertEquals(0, library.findBookByISBN(BOOK.isbn()).availableCopies());
    }

    @Test
    @Timeout(10)
    void concurrentReturnsDoNotLoseUpdates() throws Exception {
        concurrently(100, () -> {
            library.returnBook(BOOK.isbn());
            return true;
        });
        assertEquals(110, library.findBookByISBN(BOOK.isbn()).availableCopies());
    }

    @Test
    @Timeout(10)
    void concurrentDuplicateAddsHaveOneWinner() throws Exception {
        Book another = new Book("new", "New", "Author", 2020, 1);
        List<Boolean> results = concurrently(100, () -> {
            try {
                library.addBook(another);
                return true;
            } catch (LibraryException e) {
                assertEquals(LibraryErrors.BOOK_ALREADY_EXISTS, e.getLibraryError());
                return false;
            }
        });
        assertEquals(1, results.stream().filter(Boolean::booleanValue).count());
        assertEquals(another, library.findBookByISBN(another.isbn()));
    }

    private static void assertError(LibraryErrors expected, org.junit.jupiter.api.function.Executable action) {
        assertEquals(expected, assertThrows(LibraryException.class, action).getLibraryError());
    }

    private static <T> List<T> concurrently(int count, Callable<T> action) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<T>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return action.call();
                }));
            }
            start.countDown();
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                results.add(future.get());
            }
            return results;
        }
    }
}
