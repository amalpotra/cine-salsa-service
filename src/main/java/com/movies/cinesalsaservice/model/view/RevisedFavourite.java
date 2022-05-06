package com.movies.cinesalsaservice.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevisedFavourite {
    private Integer rating;
    private String comments;
}
