package com.movies.cinesalsaservice.service;

import com.movies.cinesalsaservice.exception.ResourceConflictException;
import com.movies.cinesalsaservice.exception.ResourceNotFoundException;
import com.movies.cinesalsaservice.model.Favourite;
import com.movies.cinesalsaservice.model.view.NewFavourite;
import com.movies.cinesalsaservice.model.view.RevisedFavourite;
import com.movies.cinesalsaservice.repository.FavouriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FavouriteService {
    @Autowired
    private FavouriteRepository favouriteRepository;

    public Favourite newFavourite(NewFavourite newFavourite) {
        Optional<Favourite> optionalFavourite = favouriteRepository.findFirstByContentTypeAndContentId(newFavourite.getContentType(), newFavourite.getContentId());
        if (optionalFavourite.isEmpty()) {
            return favouriteRepository.save(
                    Favourite.builder()
                            .contentType(newFavourite.getContentType())
                            .contentId(newFavourite.getContentId())
                            .rating(newFavourite.getRating())
                            .comments(newFavourite.getComments())
                            .lastModified(LocalDateTime.now())
                            .build()
            );
        }
        throw new ResourceConflictException("Favourite already exists!");
    }

    public Favourite reviseFavourite(Long favouriteId, RevisedFavourite revisedFavourite) {
        Optional<Favourite> optionalFavourite = favouriteRepository.findById(favouriteId);
        if(optionalFavourite.isPresent()) {
            Favourite favourite = optionalFavourite.get();
            return favouriteRepository.save(
                    Favourite.builder()
                            .id(favourite.getId())
                            .contentType(favourite.getContentType())
                            .contentId(favourite.getContentId())
                            .rating(revisedFavourite.getRating())
                            .comments(revisedFavourite.getComments())
                            .lastModified(LocalDateTime.now())
                            .build()
            );
        }
        throw new ResourceNotFoundException("Favourite doesn't exists!");
    }

    public void deleteFavourite(Long favouriteId) {
        Optional<Favourite> optionalFavourite = favouriteRepository.findById(favouriteId);
        if(optionalFavourite.isPresent())
            favouriteRepository.deleteById(favouriteId);
        else
            throw new ResourceNotFoundException("Favourite doesn't exists!");
    }
}