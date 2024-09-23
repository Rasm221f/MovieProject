package dat.dtos;
import dat.entities.Genre;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreDTO {

    private Integer id;
    private String name;

    // Constructor to convert Genre entity to GenreDTO
    public GenreDTO(Genre genre) {
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
