package dat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.dtos.ActorDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MovieService {

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<MovieDTO> getCountrySpecificMovies(String country, Long yearsBack) {

        List<MovieDTO> movies = new ArrayList<>();
        int currentPage = 1;
        int totalPages;

        try {
            do {
                StringBuilder builder = new StringBuilder("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=")
                        .append(currentPage)
                        .append("&with_origin_country=")
                        .append(country)
                        .append("&api_key=")
                        .append(System.getenv("api_key"));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest httpRequest = HttpRequest
                        .newBuilder()
                        .uri(new URI(builder.toString()))
                        .GET()
                        .build();

                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                JsonNode json = objectMapper.readTree(httpResponse.body());
                JsonNode results = json.get("results");

                totalPages = json.get("total_pages").asInt();
                currentPage++;

                System.out.printf("Loading: %d%%%n", (int) Math.floor((double) currentPage / totalPages * 100));

                if (httpResponse.statusCode() == 200) {
                    for (JsonNode node : results) {
                        MovieDTO movieDTO = objectMapper.treeToValue(node, MovieDTO.class);
                        if (movieDTO.getReleaseDate() != null && movieDTO.getReleaseDate().isAfter((LocalDate.now().minusYears(yearsBack))))
                            movies.add(movieDTO);
                    }
                } else {
                    System.out.println("Get request failed with status code: " + httpResponse.statusCode());
                }
            } while (currentPage <= totalPages);

            return movies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ActorDTO> getActorsFromFilm(Integer movieId) {
        List<ActorDTO> actors = new ArrayList<>();
        try {
            StringBuilder builder = new StringBuilder("https://api.themoviedb.org/3/movie/")
                    .append(movieId)
                    .append("/credits")
                    .append("?language=en-US")
                    .append("&api_key=")
                    .append(System.getenv("api_key"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(new URI(builder.toString()))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {  // Status code check should happen here
                JsonNode json = objectMapper.readTree(httpResponse.body());
                JsonNode results = json.get("cast");

                // Ensure the "cast" node exists and is an array
                if (results != null && results.isArray()) {
                    for (JsonNode node : results) {
                        ActorDTO actorDTO = objectMapper.treeToValue(node, ActorDTO.class);
                        actors.add(actorDTO);
                    }
                } else {
                    System.out.println("No cast information found.");
                }
            } else {
                System.out.println("GET request failed. Status code: " + httpResponse.statusCode());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return actors;  // Ensure this return is after the catch block
    }

    public DirectorDTO getDirectorFromFilm(Integer movieId) {
        DirectorDTO director = null;  // Initialize as null to handle cases where no director is found

        try {
            StringBuilder builder = new StringBuilder("https://api.themoviedb.org/3/movie/")
                    .append(movieId)
                    .append("/credits")
                    .append("?language=en-US")
                    .append("&api_key=")
                    .append(System.getenv("api_key"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(new URI(builder.toString()))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(httpResponse.body());
                JsonNode crew = json.get("crew");

                // Ensure the "crew" node exists and is an array
                if (crew != null && crew.isArray()) {
                    for (JsonNode node : crew) {
                        String job = node.get("job").asText();
                        if ("Director".equals(job)) {
                            director = objectMapper.treeToValue(node, DirectorDTO.class);
                            break;
                        }
                    }
                } else {
                    System.out.println("No crew information found.");
                }
            } else {
                System.out.println("GET request failed. Status code: " + httpResponse.statusCode());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return director;
    }
    public List<GenreDTO> getAllGenres() {
        List<GenreDTO> genres = new ArrayList<>();

        try {
            StringBuilder builder = new StringBuilder("https://api.themoviedb.org/3/genre/movie/list?language=en")
                    .append("&api_key=")
                    .append(System.getenv("api_key"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.valueOf(builder)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                JsonNode genreArray = json.get("genres");

                if (genreArray != null && genreArray.isArray()) {
                    for (JsonNode genreNode : genreArray) {
                        GenreDTO genre = objectMapper.treeToValue(genreNode, GenreDTO.class);
                        genres.add(genre);
                    }
                }
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return genres;
    }

}