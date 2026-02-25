package com.aioannou.library.controller;

import com.aioannou.library.data.Book;
import com.aioannou.library.data.BookRequest;
import com.aioannou.library.data.IsbnRequest;
import com.aioannou.library.exception.LibraryException;
import com.aioannou.library.service.LibraryService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A REST service for managing a library of books.
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final Bucket bucket;

    public LibraryController(final LibraryService libraryService, final Bucket bucket) {
        this.libraryService = libraryService;
        this.bucket = bucket;
    }

    /**
     * Add a book to the library.
     * @param bookRequest The book to be added
     */
    @PutMapping(value = "/add-book")
    public ResponseEntity<String> addBook(@RequestBody @Valid final BookRequest bookRequest) throws LibraryException {
        if (bucket.tryConsume(1)) {
            libraryService.addBook(bookRequest);
            return ResponseEntity.ok("Book added");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Remove a book from the library.
     * @param bookRequest The book to be removed
     */
    @DeleteMapping(value = "/remove-book")
    public ResponseEntity<String> removeBook(@RequestBody @Valid final BookRequest bookRequest) throws LibraryException {
        if (bucket.tryConsume(1)) {
            libraryService.removeBook(bookRequest);
            return ResponseEntity.ok("Book removed");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Find a book in the library given its ISBN.
     * @param isbn The ISBN of the book to be looked up
     * @return @{@link Book} the book to be returned
     */
    @GetMapping(path="/find-book-by-isbn/{isbn}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> findBookByISBN(@PathVariable("isbn") final String isbn) throws LibraryException {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(libraryService.findBookByISBN(isbn));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Find a series of books given the author
     * @param author The author of the books to be searched for
     * @return @{@link List<Book>} the list of books that were found
     */
    @GetMapping(path="/find-books-by-author/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable("author") final String author) throws LibraryException {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(libraryService.findBooksByAuthor(author));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Borrow a book from the library
     * @param isbn The ISBN of the book to be borrowed
     */
    @PostMapping(value = "/borrow-book")
    public ResponseEntity<String> borrowBook(@RequestBody @Valid final IsbnRequest isbn) throws LibraryException {
        if (bucket.tryConsume(1)) {
            libraryService.borrowBook(isbn.isbn());
            return ResponseEntity.ok("Book borrowed successfully");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Return a book to the library.
     * @param isbn the ISBN of the book to be returned
     */
    @PostMapping(value = "/return-book")
    public ResponseEntity<String> returnBook(@RequestBody @Valid final IsbnRequest isbn) throws LibraryException {
        if (bucket.tryConsume(1)) {
            libraryService.returnBook(isbn.isbn());
            return ResponseEntity.ok("Book returned successfully");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
