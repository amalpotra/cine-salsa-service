package com.movies.cinesalsaservice.model;

import com.movies.cinesalsaservice.constant.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
