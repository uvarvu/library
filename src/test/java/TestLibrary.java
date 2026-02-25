import com.aioannou.library.data.Book;
import com.aioannou.library.data.Library;
import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLibrary {

    private static Library library;

    private static final Book book = new Book("978-0241578186","The Twits","Roald Dahl",2022,10);
    private static final Book book2 = new Book("978-0241558485","Georges Marvellous Medicine","Roald Dahl",2022,10);
    private static final Book book3 = new Book("978-0141318202","Magnus Powermouse","Dick King Smith",2005,10);
    private static final Book book4 = new Book("978-0241558355","Fantastic Mr Fox","Roald Dahl",2022,10);

    @BeforeAll
    public static void setUp() throws LibraryException {
        library = new Library();
        library.clear();
        library.addBook(book);
        library.addBook(book2);
        library.addBook(book3);
    }

    @Test
    @Order(1)
    public void addBook() throws LibraryException {
        library.addBook(book4);
        Book bookFromLibrary = library.findBookByISBN("978-0241558355");
        Assertions.assertNotNull(bookFromLibrary);
        Assertions.assertEquals("Fantastic Mr Fox", bookFromLibrary.title());
    }

    @Test
    @Order(2)
    public void removeBook() throws LibraryException {
        library.removeBook(book);
        final LibraryException exception = Assertions.assertThrows(
            LibraryException.class, () -> library.findBookByISBN("978-0241578186"));
        Assertions.assertEquals(LibraryErrors.BOOK_DOES_NOT_EXIST.getErrorCode(), exception.getErrorCode());
    }

    @Test
    @Order(3)
    public void findBooksByAuthor() throws LibraryException {
        List<Book> books = library.findBooksByAuthor("Roald Dahl");
        Assertions.assertEquals(2, books.size());
    }

    @Test
    @Order(4)
    public void borrowBook() throws LibraryException {
        library.borrowBook("978-0241558485");

        Book bookFromLibrary = library.findBookByISBN("978-0241558485");
        Assertions.assertEquals("Georges Marvellous Medicine", bookFromLibrary.title());
        Assertions.assertEquals(9, bookFromLibrary.availableCopies());
    }

    @Test
    @Order(5)
    public void returnBook() throws LibraryException {
        library.returnBook("978-0241558485");

        Book bookFromLibrary = library.findBookByISBN("978-0241558485");
        Assertions.assertEquals("Georges Marvellous Medicine", bookFromLibrary.title());
        Assertions.assertEquals(10, bookFromLibrary.availableCopies());
    }
}
