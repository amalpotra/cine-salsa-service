package com.movies.cinesalsaservice.model.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RevisedFavourite {
    @Min(0)
    @Max(10)
    @NotNull(message = "rating is required")
    private Integer rating;

    @NotNull(message = "comments are required")
    private String comments;
}
