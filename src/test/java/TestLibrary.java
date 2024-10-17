import com.aioannou.library.data.Book;
import com.aioannou.library.data.Library;
import com.aioannou.library.exception.LibraryErrors;
import com.aioannou.library.exception.LibraryException;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLibrary {

    private static final Book book = new Book("978-0241578186","The Twits","Roald Dahl",2022,10);
    private static final Book book2 = new Book("978-0241558485","Georges Marvellous Medicine","Roald Dahl",2022,10);
    private static final Book book3 = new Book("978-0141318202","Magnus Powermouse","Dick King Smith",2005,10);
    private static final Book book4 = new Book("978-0241558355","Fantastic Mr Fox","Roald Dahl",2022,10);

    @BeforeAll
    public static void setUp() throws LibraryException {
        Library.INSTANCE.addBook(book);
        Library.INSTANCE.addBook(book2);
        Library.INSTANCE.addBook(book3);
    }

    @Test
    @Order(1)
    public void addBook() throws LibraryException {
        Library.INSTANCE.addBook(book4);
        Book bookFromLibrary = Library.INSTANCE.findBookByISBN("978-0241558355");
        Assertions.assertNotNull(bookFromLibrary);
        Assertions.assertEquals("Fantastic Mr Fox", bookFromLibrary.getTitle());
    }

    @Test
    @Order(2)
    public void removeBook() throws LibraryException {
        Library.INSTANCE.removeBook(book);

        try{
            Library.INSTANCE.findBookByISBN("978-0241558355");
        }
        catch (LibraryException e){
            Assertions.assertEquals(LibraryErrors.BOOK_DOES_NOT_EXIST.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    @Order(3)
    public void findBooksByAuthor() throws LibraryException {
        List<Book> books = Library.INSTANCE.findBooksByAuthor("Roald Dahl");
        Assertions.assertEquals(2, books.size());
    }

    @Test
    @Order(4)
    public void borrowBook() throws LibraryException {
        Library.INSTANCE.borrowBook("978-0241558485");

        Book bookFromLibrary = Library.INSTANCE.findBookByISBN("978-0241558485");
        Assertions.assertEquals("Georges Marvellous Medicine", bookFromLibrary.getTitle());
        Assertions.assertEquals(9, bookFromLibrary.getAvailableCopies());
    }

    @Test
    @Order(5)
    public void returnBook() throws LibraryException {
        Library.INSTANCE.returnBook("978-0241558485");

        Book bookFromLibrary = Library.INSTANCE.findBookByISBN("978-0241558485");
        Assertions.assertEquals("Georges Marvellous Medicine", bookFromLibrary.getTitle());
        Assertions.assertEquals(10, bookFromLibrary.getAvailableCopies());
    }
}