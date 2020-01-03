package wolox.training.dto;

import lombok.Data;

@Data
public class BookDTO {

    private String isbn;
    private String title;
    private String subtitle;
    private String publishers;
    private String publishDate;
    private Integer numberOfPages;
    private String authors;
}
