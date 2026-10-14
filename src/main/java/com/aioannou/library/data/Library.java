package com.aioannou.library.data;

import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe, in-memory data store. Spring creates one instance per application
 * context (the default singleton scope) and injects it into LibraryService.
 * Immutable books and atomic map operations protect concurrent updates.
 */
@Component
public final class Library {

    private final ConcurrentMap<String, Book> booksByIsbn = new ConcurrentHashMap<>();

    /**
     * Add a book to the library.
     * @param book The book to add
     * @throws LibraryException if the book already exists
     */
    public void addBook(final Book book) throws LibraryException {
        if (booksByIsbn.putIfAbsent(book.isbn(), book) != null) {
            throw new LibraryException(LibraryErrors.BOOK_ALREADY_EXISTS);
        }
    }

    /**
     * Remove a book from the library.
     * @param book The book to remove
     * @throws LibraryException if the book does not exist
     */
    public void removeBook(final Book book) throws LibraryException {
        if (booksByIsbn.remove(book.isbn()) == null) {
            throw new LibraryException(LibraryErrors.BOOK_DOES_NOT_EXIST);
        }
    }

    /**
     * Search for a book in the library.
     * @param isbn The ISBN of the book to find
     * @return the matching book
     * @throws LibraryException if the book does not exist
     */
    public Book findBookByISBN(final String isbn) throws LibraryException {
        final Book book = booksByIsbn.get(isbn);
        if (book == null) {
            throw new LibraryException(LibraryErrors.BOOK_DOES_NOT_EXIST);
        }
        return book;
    }

    /**
     * Search for a list of books given the author.
     * @param author The author to search for
     * @return matching books sorted by ISBN
     * @throws LibraryException if no books can be found
     */
    public List<Book> findBooksByAuthor(final String author) throws LibraryException {
        final List<Book> books = booksByIsbn.values().stream()
            .filter(book -> book.author().equals(author))
            .sorted(Comparator.comparing(Book::isbn))
            .toList();

        if (books.isEmpty()) {
            throw new LibraryException(LibraryErrors.AUTHOR_UNKNOWN);
        }
        return books;
    }

    /**
     * Borrow a book from the library.
     * @param isbn The ISBN of the book to borrow
     * @throws LibraryException if the book doesn't exist or there are no more copies available.
     */
    public void borrowBook(final String isbn) throws LibraryException {
        final AtomicReference<LibraryErrors> error = new AtomicReference<>();
        booksByIsbn.compute(isbn, (key, book) -> {
            if (book == null) {
                error.set(LibraryErrors.BOOK_DOES_NOT_EXIST);
                return null;
            }
            if (book.availableCopies() == 0) {
                error.set(LibraryErrors.BOOK_LOANED_OUT);
                return book;
            }
            return new Book(book.isbn(), book.title(), book.author(), book.publicationYear(), book.availableCopies() - 1);
        });

        if (error.get() != null) {
            throw new LibraryException(error.get());
        }
    }

    /**
     * Return a book to the library.
     * @param isbn The ISBN of the book to return;
     * @throws LibraryException if the book doesn't belong in the library
     */
    public void returnBook(final String isbn) throws LibraryException {
        final AtomicReference<LibraryErrors> error = new AtomicReference<>();
        booksByIsbn.compute(isbn, (key, book) -> {
            if (book == null) {
                error.set(LibraryErrors.BOOK_UNKNOWN);
                return null;
            }
            return new Book(book.isbn(), book.title(), book.author(), book.publicationYear(), book.availableCopies() + 1);
        });

        if (error.get() != null) {
            throw new LibraryException(error.get());
        }
    }

    /** Clears the store. This is not a transaction with concurrent operations. */
    public void clear() {
        booksByIsbn.clear();
    }
}
