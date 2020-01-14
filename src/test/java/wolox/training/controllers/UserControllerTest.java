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
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    private User userTest;
    private List<User> users;
    private Book bookTest;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private BookRepository mockBookRepository;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void objectMapper() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() {
        userTest = new User();
        userTest.setUsername("Username");
        userTest.setName("Name");
        users = new ArrayList<User>();
        bookTest = new Book();
        bookTest.setAuthor("Author");
        bookTest.setImage("image.png");
        bookTest.setTitle("A title");
        bookTest.setSubtitle("A subtitle");
        bookTest.setPublisher("Publisher");
        bookTest.setYear("1990");
        bookTest.setPages(100);
        bookTest.setIsbn("ABCD1234");
    }

    @Test
    public void whenCreateAUserThenUserIsPersisted() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTest)))
            .andExpect(status().isCreated());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenGetAllUsersThenReturnAListOfUsers() throws Exception {
        users.add(userTest);
        Mockito.when(mockUserRepository.findAll()).thenReturn(users);
        mvc.perform(get("/api/users/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(users)));
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenFindAUserByIdThenReturnTheUser() throws Exception {
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(userTest)));
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenUpdateAUserThenTheUserIsUpdated() throws Exception {
        userTest.setName("Other name");
        userTest.setId(1L);
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userTest)))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenDeleteAUserThenTheUserIsDeleted() throws Exception {
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(delete("/api/users/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenDeleteAUserDontExistsThenReturnNotFound() throws Exception {
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(delete("/api/users/2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenAddABookToUsersThenIsPersistedInTheUserList() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(put("/api/users/1/books").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isCreated());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenAddABookToAUsersThatNotExistsThenReturnNotFound() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(put("/api/users/2/books").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", password = "password")
    @Test
    public void whenDeleteABookThenIsPersistedInTheUserList() throws Exception {
        Mockito.when(mockBookRepository.findById(1L)).thenReturn(ofNullable(bookTest));
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(ofNullable(userTest));
        mvc.perform(delete("/api/users/1/books").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookTest)))
            .andExpect(status().isOk());
    }
}
