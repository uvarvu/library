package com.aioannou.library.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Range;

/**
 * Book Request object used for validation of incoming attributes.
 */
@NoArgsConstructor
@Data
public class BookRequest {
    @NotBlank (message = "The ISBN must not be blank")
    private String isbn;

    @NotBlank (message = "The title must not be blank")
    private String title;

    @NotBlank (message = "The author must not be blank")
    private String author;

    @Range(min= 1500, max= 2024, message="The year must be valid")
    private int publicationYear;

    @Min(value=1, message="There must be one or more available copies")
    private int availableCopies;

    public BookRequest(final String isbn, final String title, final String author, final int publicationYear, final int availableCopies){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
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