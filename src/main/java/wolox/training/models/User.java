package wolox.training.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import wolox.training.exceptions.BookAlreadyOwnedException;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthdate;

    @OneToMany(mappedBy = "user")
    private List<Book> books;

    public User() {
        this.books = new ArrayList<>();
    }

    public List<Book> getBooks() {
        return (List<Book>) Collections.unmodifiableList(books);
    }

    public void addBook(Book book) throws BookAlreadyOwnedException {
        if (books.contains(book)) {
            throw new BookAlreadyOwnedException();
        }
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }
}
