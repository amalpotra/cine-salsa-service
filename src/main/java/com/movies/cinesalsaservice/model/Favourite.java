package com.movies.cinesalsaservice.model;

import com.movies.cinesalsaservice.constant.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Favourite {
    @Id
    @GeneratedValue
    private Long id;
    private ContentType contentType;
    private Long contentId;
    private Integer rating;
    private String comments;
    private LocalDateTime lastModified;
}
