package com.movies.cinesalsaservice.service.external;

import com.movies.cinesalsaservice.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Component
public class WebClientFactory {
    @Value("${apiKey}")
    private String API_KEY;

    public WebClient getWebClientForMovie() {
        return WebClient.create("https://api.themoviedb.org/3/movie");
    }

    @PostConstruct
    private void init() {
        WebClient.create("https://api.themoviedb.org/3/healthcheck")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("api_key", API_KEY)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ServiceUnavailableException("The MovieDB says: " + response.statusCode());
                })
                .toBodilessEntity()
                .block();
        System.out.println("The MovieDB is up and running!");
    }
}
