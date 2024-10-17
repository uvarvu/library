package com.aioannou.library.service;

import com.aioannou.library.data.Book;
import com.aioannou.library.data.BookRequest;
import com.aioannou.library.data.Library;
import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  The Library's service class.  Keeps the business logic out of the controller.
 */
@Service
public class LibraryService {

    /**
     * Validate request and add a book.
     */
    public ResponseEntity<String> addBook(final BookRequest bookRequest){
        try{
            final Book book = new Book(bookRequest.getIsbn(), bookRequest.getTitle(), bookRequest.getAuthor(), bookRequest.getPublicationYear(), bookRequest.getAvailableCopies());
            Library.INSTANCE.addBook(book);
            return ResponseEntity.ok("Book added");
        }
        catch (LibraryException e){
            return new ResponseEntity<>("Book already exists", HttpStatus.CONFLICT);
        }
    }

    /**
     * Validate request and remove a book.
     */
    public ResponseEntity<String> removeBook(final BookRequest bookRequest){
        try{
            final Book book = new Book(bookRequest.getIsbn(), bookRequest.getTitle(), bookRequest.getAuthor(), bookRequest.getPublicationYear(), bookRequest.getAvailableCopies());
            Library.INSTANCE.removeBook(book);
            return ResponseEntity.ok("Book removed");
        }
        catch (LibraryException e){
            return new ResponseEntity<>("Book does not exist", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find a book given its ISBN.
     */
    public ResponseEntity<Book> findBookByISBN(final String isbn){
        try{
            Book book = Library.INSTANCE.findBookByISBN(isbn);
            return ResponseEntity.ok(book);
        }
        catch (LibraryException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find a series of books given the author.
     */
    public ResponseEntity<List<Book>> findBooksByAuthor(final String author){
        try{
            List<Book> books = Library.INSTANCE.findBooksByAuthor(author);
            return ResponseEntity.ok(books);
        }
        catch (LibraryException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Borrow a book given its ISBN.
     */
    public ResponseEntity<String> borrowBook(final String isbn){
        try{
            Library.INSTANCE.borrowBook(isbn);
            return ResponseEntity.ok("Book borrowed successfully");
        }
        catch (LibraryException e){
            if (e.getErrorCode() == LibraryErrors.BOOK_DOES_NOT_EXIST.getErrorCode()){
                return new ResponseEntity<>("Book does not exist", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Book is already loaned out", HttpStatus.CONFLICT);
        }
    }

    /**
     * Return a book given its ISBN.
     */
    public ResponseEntity<String> returnBook(final String isbn){
        try{
            Library.INSTANCE.returnBook(isbn);
            return ResponseEntity.ok("Book returned successfully");
        }
        catch (LibraryException e){
            return new ResponseEntity<>("Book does not belong to this library", HttpStatus.NOT_FOUND);
        }
    }
}