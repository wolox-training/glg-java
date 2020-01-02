package wolox.training.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import wolox.training.exceptions.BookIdDontMatchException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookRepository bookRepository;

    private OpenLibraryService openLibraryService;

    @Autowired
    public BookController(BookRepository bookRepository, OpenLibraryService openLibraryService) {
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
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
    @ApiOperation(value = "Giving an id, return the book", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Succesfully retrived book"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing th resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public Book findOne(
        @ApiParam(value = "id to find the book", required = true) @PathVariable Long id)
        throws BookNotFoundException {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdDontMatchException();
        }
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
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
                openLibraryService.findByIsbn(isbn).orElseThrow(BookNotFoundException::new));
        }
    }
}
