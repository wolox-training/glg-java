package wolox.training.controllers;

import static java.util.Optional.ofNullable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    private Book bookTest;
    private Book book1994;
    private List<Book> books;

    @MockBean
    private BookRepository mockBookRepository;

    @MockBean
    private OpenLibraryService mockOpenLibraryService;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void objectMapper() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() {
        bookTest = new Book();
        bookTest.setAuthor("Author");
        bookTest.setImage("image.png");
        bookTest.setTitle("A title");
        bookTest.setSubtitle("A subtitle");
        bookTest.setPublisher("Publisher");
        bookTest.setYear("1990");
        bookTest.setPages(100);
        bookTest.setIsbn("ABCD1234");
        book1994 = new Book();
        book1994.setAuthor("Author");
        book1994.setImage("image.png");
        book1994.setTitle("A title");
        book1994.setSubtitle("A subtitle");
        book1994.setPublisher("Publisher");
        book1994.setYear("1994");
        book1994.setPages(100);
        book1994.setIsbn("ABCD1235");
        books = new ArrayList<Book>();

    }

    @Test
    public void whenCreateABookThenBookIsPersisted() throws Exception {
        mvc.perform(post("/api/books/").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isCreated());
    }

    @Test
    public void whenFindABook() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(bookTest)));
    }

    @Test
    public void whenFindABookThatNotExists() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void whenUpdateABook() throws Exception {
        bookTest.setTitle("Another title");
        bookTest.setId(1L);
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(put("/api/books/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteABook() throws Exception {
        bookTest.setId(1L);
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(delete("/api/books/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void whenFindABookByYear() throws Exception {
        books.add(book1994);
        Mockito.when(mockBookRepository.findByYearAllIgnoreCase("1994")).thenReturn(books);
        mvc.perform(get("/api/books/?yearh=1994").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void whenFindABookByIsbn() throws Exception {
        Mockito.when(mockBookRepository.findByIsbn("ABCD1234")).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/?yearh=1994").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
