package com.movies.cinesalsaservice.model.view;

import com.movies.cinesalsaservice.constant.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class NewFavourite {
    @NotNull(message = "contentType is required")
    private ContentType contentType;

    @NotNull(message = "contentId required")
    private Long contentId;

    @Min(0)
    @Max(10)
    @NotNull(message = "rating is required")
    private Integer rating;

    @NotNull(message = "comments are required")
    private String comments;
}
