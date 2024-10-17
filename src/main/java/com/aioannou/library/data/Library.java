package com.aioannou.library.data;

import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread Safe library data store.
 */
public enum Library {
    INSTANCE;

    /**
     * Add a book to the library.
     * @param book The book to add
     * @throws LibraryException if the book already exists
     */
    public synchronized void addBook(final Book book) throws LibraryException{
        if(bookdb.containsKey(book.getIsbn())){
            throw new LibraryException(LibraryErrors.BOOK_ALREADY_EXISTS);
        }
        bookdb.put(book.getIsbn(), book);
    }

    /**
     * Remove a book from the library.
     * @param book The book to remove
     * @throws LibraryException if the book does not exist
     */
    public synchronized void removeBook(final Book book) throws LibraryException{
        if(!bookdb.containsKey(book.getIsbn())){
            throw new LibraryException(LibraryErrors.BOOK_DOES_NOT_EXIST);
        }
        bookdb.remove(book.getIsbn());
    }

    /**
     * Search for a book in the library.
     * @param isbn The ISBN of the book to find
     * @return @{@link Book} the book to return
     * @throws LibraryException if the book does not exist
     */
    public synchronized Book findBookByISBN(final String isbn) throws LibraryException{
        if(!bookdb.containsKey(isbn)){
            throw new LibraryException(LibraryErrors.BOOK_DOES_NOT_EXIST);
        }
        return bookdb.get(isbn);
    }

    /**
     * Search for a list of books given the author.
     * @param author The author to search for
     * @return @List<{@link Book}> The list of books to return
     * @throws LibraryException if no books can be found
     */
    public synchronized List<Book> findBooksByAuthor(final String author) throws LibraryException{
        List<Book> books = bookdb.values().stream()
            .filter(book -> book.getAuthor().equals(author))
            .toList();

        if(books.isEmpty()){
            throw new LibraryException(LibraryErrors.AUTHOR_UNKNOWN);
        }
        return books;
    }

    /**
     * Borrow a book from the library.
     * @param isbn The ISBN of the book to borrow
     * @throws LibraryException if the book doesn't exist or there are no more copies available.
     */
    public synchronized void borrowBook(final String isbn) throws LibraryException{
        if(!bookdb.containsKey(isbn)){
            throw new LibraryException(LibraryErrors.BOOK_DOES_NOT_EXIST);
        }
        Book book = bookdb.get(isbn);
        if (book.getAvailableCopies() == 0){
            throw new LibraryException(LibraryErrors.BOOK_LOANED_OUT);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookdb.put(isbn, book);
    }

    /**
     * Return a book to the library.
     * @param isbn The ISBN of the book to return;
     * @throws LibraryException if the book doesn't belong in the library
     */
    public synchronized void returnBook(final String isbn) throws LibraryException{
        if(!bookdb.containsKey(isbn)){
            throw new LibraryException(LibraryErrors.BOOK_UNKNOWN);
        }
        Book book = bookdb.get(isbn);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookdb.put(isbn, book);
    }

    private final Map<String, Book> bookdb = new HashMap<>();
}