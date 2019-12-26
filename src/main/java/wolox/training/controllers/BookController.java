package wolox.training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/books")
public class BookController {

    private BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting " + name;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(BootstrapMethodError::new);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Book id don't match");
        }
        bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        bookRepository.deleteById(id);
    }
}
