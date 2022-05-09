package com.movies.cinesalsaservice.service.external;

import com.movies.cinesalsaservice.exception.FailedDependencyException;
import com.movies.cinesalsaservice.model.external.ExternalMovie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ExternalMovieService {
    private final WebClientFactory webClientFactory;
    @Value("${apiKey}")
    private String API_KEY;

    public ExternalMovieService(WebClientFactory webClientFactory) {
        this.webClientFactory = webClientFactory;
    }

    public ExternalMovie fetchMovie(Long movieId) {
        return webClientFactory.getWebClientForMovie()
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
