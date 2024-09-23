package dat.entities;

import dat.dtos.GenreDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Genre {

    @Id
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies;

    public Genre(GenreDTO genreDTO) {
        this.id = genreDTO.getId();
        this.name = genreDTO.getName();
    }
}
