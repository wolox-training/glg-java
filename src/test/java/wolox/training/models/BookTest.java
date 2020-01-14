package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
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
        Faker faker = new Faker();
        book = new Book();
        book.setAuthor(faker.book().author());
        book.setImage(faker.internet().image());
        book.setTitle(faker.book().title());
        book.setSubtitle(faker.book().title());
        book.setPublisher(faker.book().publisher());
        book.setYear(String.valueOf(faker.number().numberBetween(1900, 2020)));
        book.setPages(Integer.parseInt(faker.number().digits(3)));
        book.setIsbn(faker.code().isbn10());
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
