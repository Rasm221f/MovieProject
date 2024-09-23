package dat;

import dat.config.HibernateConfig;
import dat.daos.MovieDAO;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_project");
        MovieDAO movieDAO = new MovieDAO(emf);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MovieService movieService = new MovieService(objectMapper);

//        //Populate genre table
//        List<GenreDTO> genreDTOs = movieService.getAllGenres();
//        for (GenreDTO genreDTO : genreDTOs) {
//            System.out.println(genreDTO);
//            movieDAO.createGenres(genreDTO);
//        }

         List<MovieDTO> danishMovies = movieService.getCountrySpecificMovies("DK", 5L);



        for (MovieDTO m : danishMovies) {
            DirectorDTO directorDTO = movieService.getDirectorFromFilm(m.getId());
            if (directorDTO != null) {
                m.setDirectorDTO(directorDTO);
            }
            List<ActorDTO> actorDTOs = movieService.getActorsFromFilm(m.getId());
            if (!actorDTOs.isEmpty()) {
                m.setActorDTOs(actorDTOs);
            }

            List<GenreDTO> allGenres = movieService.getAllGenres();
            // Map movie genres by comparing the genre IDs
            List<GenreDTO> movieGenres = m.getGenreIds().stream()
                    .map(genreId -> allGenres.stream()
                            .filter(g -> g.getId().equals(genreId))
                            .findFirst()
                            .orElse(null)) // If no match is found, returns null
                    .filter(Objects::nonNull) // Filter out any null values
                    .collect(Collectors.toList());

            // Set the genres for each movie
            m.setGenreDTOs(movieGenres);
            movieDAO.createMovie(m);
        }
   }
}