package wolox.training.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class BookDTO implements Serializable {

    private String isbn;
    private String title;
    private String subtitle;
    private String publishers;
    private String publishDate;
    private Integer numberOfPages;
    private String authors;
}
