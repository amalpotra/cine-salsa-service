package com.movies.cinesalsaservice.model.view;

import com.movies.cinesalsaservice.constant.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewFavourite {
    private ContentType contentType;
    private Long contentId;
    private Integer rating;
    private String comments;
}
