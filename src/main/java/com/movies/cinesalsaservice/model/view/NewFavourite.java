package com.movies.cinesalsaservice.model.view;

import com.movies.cinesalsaservice.constant.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewFavourite {
    private ContentType contentType;
    private Long contentId;
    private Integer rating;
    private String comments;
}
