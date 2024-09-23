package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Director;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorDTO {

    private Integer id;
    private String name;
    private String job;

    // Constructor to convert Director entity to DirectorDTO
    public DirectorDTO(Director director) {
        this.id = director.getId();
        this.name = director.getName();
    }
}