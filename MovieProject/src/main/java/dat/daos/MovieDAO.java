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

            // Check if movie with the same title and release date already exists
            Movie existingMovie = em.createQuery(
                            "SELECT m FROM Movie m WHERE m.title = :title AND m.releaseDate = :releaseDate", Movie.class)
                    .setParameter("title", movieDTO.getTitle())
                    .setParameter("releaseDate", movieDTO.getReleaseDate())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingMovie != null) {
                System.out.println("Movie already exists in the database.");
                em.getTransaction().rollback();
                return;
            }

            movie.setTitle(movieDTO.getTitle());
            movie.setLanguage(movieDTO.getLanguage());
            movie.setOverview(movieDTO.getOverview());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setRating(movieDTO.getRating());

            // Check if Director already exists by ID, or persist if new
            if (movieDTO.getDirectorDTO() != null) {
                Director director = em.find(Director.class, movieDTO.getDirectorDTO().getId());
                if (director == null) {
                    director = new Director();
                    director.setName(movieDTO.getDirectorDTO().getName());
                    director.setId(movieDTO.getDirectorDTO().getId());
                    em.persist(director);
                }
                movie.setDirector(director); // Set director (existing or newly persisted)
            }

            // Convert List<ActorDTO> to List<Actor> and set in Movie entity
            List<Actor> actors = movieDTO.getActorDTOs().stream()
                    .map(actorDTO -> {
                        // Check if actor exists by ID
                        Actor actor = em.find(Actor.class, actorDTO.getId());
                        if (actor == null) {
                            actor = new Actor();
                            actor.setId(actorDTO.getId()); // Ensure ID is set
                            actor.setName(actorDTO.getName());
                            em.persist(actor); // Persist only if actor is new
                        }
                        return actor;
                    })
                    .collect(Collectors.toList());
            movie.setActors(actors);

            // Convert List<GenreDTO> to List<Genre> and set in Movie entity
            List<Genre> genres = movieDTO.getGenreDTOs().stream()
                    .map(genreDTO -> em.getReference(Genre.class, genreDTO.getId())) // Use getReference to assume genres already exist
                    .collect(Collectors.toList());
            movie.setGenres(genres);

            // Persist the Movie entity
            em.persist(movie);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}