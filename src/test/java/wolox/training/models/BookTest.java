package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.repositories.BookRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BookTest {

    private Book book;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Before
    public void setUp() {
        book = new Book();
        book.setAuthor("Author");
        book.setImage("image.png");
        book.setTitle("A title");
        book.setSubtitle("A subtitle");
        book.setPublisher("Publisher");
        book.setYear("1990");
        book.setPages(100);
        book.setIsbn("ABCD1234");
        bookRepository.save(book);
    }

    @Test
    public void whenCreateABookThenIsPersisted() {
        entityManager.persist(book);
        entityManager.flush();

        Book bookFounded = bookRepository.findByIsbn(book.getIsbn()).get();
        assertThat(bookFounded.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookFounded.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(bookFounded.getImage()).isEqualTo(book.getImage());
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateABookWithoutIsbn() {
        book.setIsbn(null);
        bookRepository.save(book);
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateABookWithNoPages() {
        book.setPages(null);
        bookRepository.save(book);
    }
}
