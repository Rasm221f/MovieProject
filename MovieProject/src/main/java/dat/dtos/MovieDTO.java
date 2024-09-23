package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Movie;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private Integer id;

    @JsonProperty("original_title")
    private String title;

    @JsonProperty("original_language")
    private String language;

    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    @JsonProperty("vote_average")
    private Double rating;

    private List<GenreDTO> genreDTOs;

    public String getReleaseYear() {
        return String.valueOf(releaseDate.getYear());
    }

    private List<ActorDTO> actorDTOs;
    private DirectorDTO directorDTO;

    // Constructor that transforms the Movie entity into a MovieDTO
    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.language = movie.getLanguage();
        this.releaseDate = movie.getReleaseDate();
        this.rating = movie.getRating();

        // Convert Genre entities to GenreDTOs
        this.genreDTOs = movie.getGenres().stream()
                .map(GenreDTO::new)
                .collect(Collectors.toList());

        // Convert Actor entities to ActorDTOs
        this.actorDTOs = movie.getActors().stream()
                .map(ActorDTO::new)
                .collect(Collectors.toList());

        // Convert Director entity to DirectorDTO (if present)
        this.directorDTO = movie.getDirector() != null ? new DirectorDTO(movie.getDirector()) : null;
    }
}

