package com.aioannou.library.controller;

import com.aioannou.library.data.Book;
import com.aioannou.library.data.BookRequest;
import com.aioannou.library.service.LibraryService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * A REST service for managing a library of books.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    private final Bucket bucket;

    /**
     * Constructor that sets up the rate limit.   20 API calls to start with, ten tokens replenish every five seconds.
     */
    public LibraryController(){
        this.bucket = Bucket.builder()
            .addLimit(limit -> limit.capacity(20).refillGreedy(10, Duration.ofSeconds(5)))
            .build();
    }

    /**
     * Add a book to the library.
     * @param bookRequest The book to be added
     */
    @PutMapping(value = "/add-book")
    @ResponseBody
    public ResponseEntity<String> addBook(@RequestBody @Valid final BookRequest bookRequest) {
        if(bucket.tryConsume(1)){
            return libraryService.addBook(bookRequest);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Remove a book from the library.
     * @param bookRequest The book to be removed
     */
    @DeleteMapping(value = "/remove-book")
    @ResponseBody
    public ResponseEntity<String> removeBook(@RequestBody @Valid final BookRequest bookRequest) {
        if(bucket.tryConsume(1)){
            return libraryService.removeBook(bookRequest);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Find a book in the library given its ISBN.
     * @param isbn The ISBN of the book to be looked up
     * @return @{@link Book} the book to be returned
     */
    @GetMapping(path="/find-book-by-isbn/{isbn}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Book> findBookByISBN(@PathVariable("isbn") final String isbn) {
        if(bucket.tryConsume(1)){
            return libraryService.findBookByISBN(isbn);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Find a series of books given the author
     * @param author The author of the books to be searched for
     * @return @{@link List<Book>} the list of books that were found
     */
    @GetMapping(path="/find-books-by-author/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable("author") final String author) {
        if(bucket.tryConsume(1)){
            return libraryService.findBooksByAuthor(author);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Borrow a book from the library
     * @param isbn The ISBN of the book to be borrowed
     */
    @PostMapping(value = "/borrow-book")
    @ResponseBody
    public ResponseEntity<String> deleteBook( @RequestBody String isbn) {
        if(bucket.tryConsume(1)){
            return libraryService.borrowBook(isbn);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    /**
     * Return a book to the library.
     * @param isbn the ISBN of the book to be returned
     */
    @PostMapping(value = "/return-book")
    @ResponseBody
    public ResponseEntity<String> returnBook( @RequestBody String isbn) {
        if(bucket.tryConsume(1)){
            return libraryService.returnBook(isbn);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
