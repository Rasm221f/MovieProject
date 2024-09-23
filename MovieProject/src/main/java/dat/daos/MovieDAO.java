package dat.daos;

import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class MovieDAO {


    private EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void createGenres(GenreDTO genreDTO){
        Genre genre = new Genre();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            genre.setId(genreDTO.getId());
            genre.setName(genreDTO.getName());
            em.persist(genre);
            em.getTransaction().commit();
        }
    }

    public void createMovie(MovieDTO movieDTO) {
        // Convert MovieDTO to Movie entity
        Movie movie = new Movie();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            movie.setTitle(movieDTO.getTitle());
            movie.setLanguage(movieDTO.getLanguage());
            movie.setOverview(movieDTO.getOverview());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setRating(movieDTO.getRating());

            // Convert DirectorDTO to Director entity and set it in Movie entity
            if (movieDTO.getDirectorDTO() != null) {
                Director director = new Director();
                director.setName(movieDTO.getDirectorDTO().getName());
                director.setId(movieDTO.getDirectorDTO().getId());
                movie.setDirector(director);

                // Persist the director if it's a new one (optional)
                em.persist(director);
            }

            // Convert List<ActorDTO> to List<Actor> and set in Movie entity
            List<Actor> actors = movieDTO.getActorDTOs().stream()
                    .map(actorDTO -> {
                        Actor actor = new Actor();
                        actor.setName(actorDTO.getName());
                        return actor;
                    })
                    .collect(Collectors.toList());
            movie.setActors(actors);

            // Persist each actor (optional)
            for (Actor actor : actors) {
                em.persist(actor);
            }


            // Convert List<GenreDTO> to List<Genre> and set in Movie entity
            List<Genre> genres = movieDTO.getGenreDTOs().stream()
                    .map(genreDTO -> {
                        Genre genre = new Genre();
                        genre.setId(genreDTO.getId());  // Assuming the genre already exists with this ID
                        genre.setName(genreDTO.getName());
                        return genre;
                    })
                    .collect(Collectors.toList());
            movie.setGenres(genres);

            // Persist the Movie entity
            em.persist(movie);
        }
    }


}