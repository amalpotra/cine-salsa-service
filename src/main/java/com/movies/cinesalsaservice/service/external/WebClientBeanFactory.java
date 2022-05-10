package com.movies.cinesalsaservice.service.external;

import com.movies.cinesalsaservice.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Component
public class WebClientBeanFactory {
    private final WebClient.Builder webClientBuilder;
    @Value("${apiKey}")
    private String API_KEY;

    public WebClientBeanFactory(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public WebClient webClientForMovie() {
        return webClientBuilder.baseUrl("https://api.themoviedb.org/3/movie").build();
    }

    @PostConstruct
    private void init() {
        webClientBuilder
                .baseUrl("https://api.themoviedb.org/3/healthcheck")
                .build()
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
