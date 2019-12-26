package wolox.training.controllers;

import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RestController
@RequestMapping("/books")
public class BookController {

    private BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    private OpenLibraryService openLibraryService;

    @Bean
    private OpenLibraryService openLibraryService() {
        return new OpenLibraryService();
    }

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name = "name", required = false, defaultValue = "World") String name,
        Model model) {
        model.addAttribute("name", name);
        return "greeting " + name;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) throws IOException {
        return bookRepository.findById(id).orElseThrow(this::bookNotFound);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Book id don't match");
        }
        bookRepository.findById(id).orElseThrow(this::bookNotFound);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id).orElseThrow(this::bookNotFound);
        bookRepository.deleteById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Book> search(@RequestParam(name = "isbn", required = false) String isbn)
        throws IOException {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        if (optionalBook.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optionalBook.get());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                openLibraryService.findByIsbn(isbn).orElseThrow(this::bookNotFound));
        }
    }

    private ResponseStatusException bookNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
    }
}
