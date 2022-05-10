package com.movies.cinesalsaservice.service.external;

import com.movies.cinesalsaservice.exception.FailedDependencyException;
import com.movies.cinesalsaservice.model.external.ExternalMovie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExternalMovieService {
    private final WebClient webClientForMovie;
    @Value("${apiKey}")
    private String API_KEY;

    public ExternalMovieService(WebClient webClientForMovie) {
        this.webClientForMovie = webClientForMovie;
    }

    public ExternalMovie fetchMovie(Long movieId) {
        return webClientForMovie
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{movieId}")
                        .queryParam("api_key", API_KEY)
                        .build(movieId)
                )
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new FailedDependencyException("The MovieDB says: " + response.statusCode());
                })
                .bodyToMono(ExternalMovie.class)
                .block();
    }
}
