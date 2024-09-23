package dat.entities;

import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {

    @Id
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "director")
    private List<Movie> movies;

    public Director(DirectorDTO directorDTO) {
        this.id = directorDTO.getId();
        this.name = directorDTO.getName();
    }
}

