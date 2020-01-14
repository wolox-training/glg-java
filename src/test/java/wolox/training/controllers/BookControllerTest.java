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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
@ContextConfiguration(classes = {BookController.class})
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    private Book bookTest;
    private Book bookWiyhYear1994;
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
        bookWiyhYear1994 = new Book();
        bookWiyhYear1994.setAuthor("Author");
        bookWiyhYear1994.setImage("image.png");
        bookWiyhYear1994.setTitle("A title");
        bookWiyhYear1994.setSubtitle("A subtitle");
        bookWiyhYear1994.setPublisher("Publisher");
        bookWiyhYear1994.setYear("1994");
        bookWiyhYear1994.setPages(100);
        bookWiyhYear1994.setIsbn("ABCD1235");
        books = new ArrayList<Book>();

    }

    @Test
    public void whenCreateABookThenBookIsPersisted() throws Exception {
        mvc.perform(post("/api/books/").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isCreated());
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateABookWithoutIsbnThenBookIsNotPersisted() throws Exception {
        bookTest.setIsbn(null);
        mvc.perform(post("/api/books/").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)));
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenFindABookByIdThenReturnTheBook() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(bookTest)));
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenFindABookThatNotExistsThenReturnNotFound() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenUpdateABookThenIsUpdated() throws Exception {
        bookTest.setTitle("Another title");
        bookTest.setId(1L);
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(put("/api/books/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenDeleteABookThenTheBookIsDeleted() throws Exception {
        bookTest.setId(1L);
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        mvc.perform(delete("/api/books/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenFindABookByYearThenReturnAListWithTheBook() throws Exception {
        books.add(bookWiyhYear1994);
        Mockito.when(mockBookRepository.findByYearAllIgnoreCase("1994")).thenReturn(books);
        mvc.perform(get("/api/books/?year=1994").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenFindABookByIsbnThenReturnTheBook() throws Exception {
        Mockito.when(mockBookRepository.findByIsbn("ABCD1234")).thenReturn(ofNullable(bookTest));
        mvc.perform(get("/api/books/?isbn=ABCD1234").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
