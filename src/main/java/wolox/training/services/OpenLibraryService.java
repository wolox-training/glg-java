package wolox.training.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import wolox.training.dto.BookDTO;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

public class OpenLibraryService {

    @Autowired
    private BookRepository bookRepository;

    public Optional<Book> findByIsbn(String isbn) throws IOException {
        Optional<BookDTO> optionalBookDTO = bookInfo(isbn);
        return optionalBookDTO.map((bookDTO) -> {
            Book book = convertDtoToEntity(bookDTO);
            bookRepository.save(book);
            return book;
        });
    }

    public Optional<BookDTO> bookInfo(String isbn) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String urlString = "https://openlibrary.org/api/books?bibkeys=";
        urlString += String.format("ISBN:%s&format=json&jscmd=data", isbn);
        ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Objects.requireNonNull(response.getBody()));
        if (root.size() > 0) {
            return Optional.of(buildDto(isbn, root));
        } else {
            return Optional.empty();
        }
    }

    private BookDTO buildDto(String isbn, JsonNode root) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setIsbn(isbn);
        bookDTO.setTitle(getValueFromJsonNode(root, "title"));
        bookDTO.setSubtitle(getValueFromJsonNode(root, "subtitle"));
        bookDTO.setPublishers(getValueFromJsonNode(root, "publishers"));
        bookDTO.setPublishDate(getValueFromJsonNode(root, "publish_date"));
        bookDTO.setNumberOfPages(Integer.parseInt(getValueFromJsonNode(root, "number_of_pages")));
        bookDTO.setAuthors(root.findPath("authors").findPath("name").textValue());
        return bookDTO;
    }

    private String getValueFromJsonNode(JsonNode root, String key) {
        return String.join(", ", root.findValuesAsText(key));
    }

    private Book convertDtoToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setAuthor(bookDTO.getAuthors());
        book.setImage("");
        book.setTitle(bookDTO.getTitle());
        book.setSubtitle(bookDTO.getSubtitle());
        book.setPublisher(bookDTO.getPublishers());
        book.setYear(bookDTO.getPublishDate());
        book.setPages(bookDTO.getNumberOfPages());
        book.setIsbn(bookDTO.getIsbn());
        return book;
    }
}
