package com.movies.cinesalsaservice.repository;

import com.movies.cinesalsaservice.constant.ContentType;
import com.movies.cinesalsaservice.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long>  {
    Optional<Favourite> findFirstByContentTypeAndContentId(ContentType contentType, Long contentId);
}
