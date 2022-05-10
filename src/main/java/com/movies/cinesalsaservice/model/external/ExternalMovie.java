package com.movies.cinesalsaservice.model.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExternalMovie {
    private Boolean adult;
    private String backdrop_path;
    private List<Genre> genres;
    private Long id;
    private String original_language;
    private String original_title;
    private String overview;
    private Float popularity;
    private String poster_path;
    private LocalDate release_date;
    private String title;
    private Boolean video;
    private Float vote_average;
    private Long vote_count;
}
