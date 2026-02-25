package com.aioannou.library.controller;

import com.aioannou.library.data.Book;
import com.aioannou.library.data.Library;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibraryControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private Library library;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    void setUp() throws Exception {
        library.clear();
        library.addBook(new Book("978-0000000001", "Existing Book", "Test Author", 2023, 2));
    }

    @Test
    void addAndFindBook() throws Exception {
        final String requestBody = """
            {
              "isbn": "978-0000000002",
              "title": "Clean Code",
              "author": "Robert C. Martin",
              "publicationYear": 2008,
              "availableCopies": 3
            }
            """;

        mockMvc.perform(put("/library/add-book")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().string("Book added"));

        mockMvc.perform(get("/library/find-book-by-isbn/{isbn}", "978-0000000002"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isbn").value("978-0000000002"))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.availableCopies").value(3));
    }

    @Test
    void borrowAndReturnBookWithIsbnRequest() throws Exception {
        final String isbnBody = """
            {
              "isbn": "978-0000000001"
            }
            """;

        mockMvc.perform(post("/library/borrow-book")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(isbnBody)))
            .andExpect(status().isOk())
            .andExpect(content().string("Book borrowed successfully"));

        mockMvc.perform(get("/library/find-book-by-isbn/{isbn}", "978-0000000001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableCopies").value(1));

        mockMvc.perform(post("/library/return-book")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(isbnBody)))
            .andExpect(status().isOk())
            .andExpect(content().string("Book returned successfully"));

        mockMvc.perform(get("/library/find-book-by-isbn/{isbn}", "978-0000000001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void borrowBookWithBlankIsbnReturnsBadRequest() throws Exception {
        final String invalidBody = """
            {
              "isbn": ""
            }
            """;

        mockMvc.perform(post("/library/borrow-book")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isbn").value("The ISBN must not be blank"));
    }

    @Test
    void findUnknownBookReturnsNotFound() throws Exception {
        mockMvc.perform(get("/library/find-book-by-isbn/{isbn}", "978-9999999999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("This book does not exist in the database"));
    }

}
