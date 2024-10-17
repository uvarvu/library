package com.aioannou.library.data;

import lombok.*;

import java.io.Serializable;

/**
 * A Book object.
 */
@NoArgsConstructor
@Data
public class Book implements Serializable {

    private String isbn;

    private String title;

    private String author;

    private int publicationYear;

    private int availableCopies;

    public Book(final String isbn, final String title, final String author, final int publicationYear, final int availableCopies){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    public void setIsbn(final String isbn){
        this.isbn = isbn;
    }

    public String getIsbn(){
        return isbn;
    }

    public void setTitle(final String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setAuthor(final String author){
        this.author = author;
    }

    public String getAuthor(){
        return author;
    }

    public void setPublicationYear(final int publicationYear){
        this.publicationYear = publicationYear;
    }

    public int getPublicationYear(){
        return publicationYear;
    }

    public void setAvailableCopies(final int availableCopies){
        this.availableCopies = availableCopies;
    }

    public int getAvailableCopies(){
        return availableCopies;
    }
}