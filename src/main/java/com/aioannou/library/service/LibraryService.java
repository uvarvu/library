package com.aioannou.library.service;

import com.aioannou.library.data.Book;
import com.aioannou.library.data.BookRequest;
import com.aioannou.library.data.Library;
import com.aioannou.library.exception.LibraryException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  The Library's service class.  Keeps the business logic out of the controller.
 */
@Service
public class LibraryService {

    private final Library library;

    public LibraryService(final Library library) {
        this.library = library;
    }

    /**
     * Validate request and add a book.
     */
    public void addBook(final BookRequest bookRequest) throws LibraryException {
        final Book book = new Book(
            bookRequest.isbn(),
            bookRequest.title(),
            bookRequest.author(),
            bookRequest.publicationYear(),
            bookRequest.availableCopies()
        );
        library.addBook(book);
    }

    /**
     * Validate request and remove a book.
     */
    public void removeBook(final BookRequest bookRequest) throws LibraryException {
        final Book book = new Book(
            bookRequest.isbn(),
            bookRequest.title(),
            bookRequest.author(),
            bookRequest.publicationYear(),
            bookRequest.availableCopies()
        );
        library.removeBook(book);
    }

    /**
     * Find a book given its ISBN.
     */
    public Book findBookByISBN(final String isbn) throws LibraryException {
        return library.findBookByISBN(isbn);
    }

    /**
     * Find a series of books given the author.
     */
    public List<Book> findBooksByAuthor(final String author) throws LibraryException {
        return library.findBooksByAuthor(author);
    }

    /**
     * Borrow a book given its ISBN.
     */
    public void borrowBook(final String isbn) throws LibraryException {
        library.borrowBook(isbn);
    }

    /**
     * Return a book given its ISBN.
     */
    public void returnBook(final String isbn) throws LibraryException {
        library.returnBook(isbn);
    }
}
