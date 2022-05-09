package com.movies.cinesalsaservice.service;

import com.movies.cinesalsaservice.constant.ContentType;
import com.movies.cinesalsaservice.exception.ResourceConflictException;
import com.movies.cinesalsaservice.exception.ResourceNotFoundException;
import com.movies.cinesalsaservice.model.Favourite;
import com.movies.cinesalsaservice.model.Movie;
import com.movies.cinesalsaservice.model.external.ExternalMovie;
import com.movies.cinesalsaservice.model.external.Genre;
import com.movies.cinesalsaservice.model.view.NewFavourite;
import com.movies.cinesalsaservice.model.view.RevisedFavourite;
import com.movies.cinesalsaservice.repository.FavouriteRepository;
import com.movies.cinesalsaservice.service.external.ExternalMovieService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final ExternalMovieService externalMovieService;

    public FavouriteService(FavouriteRepository favouriteRepository, ExternalMovieService externalMovieService) {
        this.favouriteRepository = favouriteRepository;
        this.externalMovieService = externalMovieService;
    }

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
        if (optionalFavourite.isPresent()) {
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
        if (optionalFavourite.isPresent())
            favouriteRepository.deleteById(favouriteId);
        else
            throw new ResourceNotFoundException("Favourite doesn't exists!");
    }

    public Movie getContent(Long favouriteId, ContentType contentType) {
        Optional<Favourite> optionalFavourite = favouriteRepository.findById(favouriteId);
        if (optionalFavourite.isPresent()) {
            Favourite favourite = optionalFavourite.get();
            if (contentType == ContentType.MOVIE) {
                ExternalMovie externalMovie = externalMovieService.fetchMovie(favourite.getContentId());
                return Movie.builder()
                        .adult(externalMovie.getAdult())
                        .backdrop_path(externalMovie.getBackdrop_path())
                        .genre_ids(externalMovie.getGenres().stream().map(Genre::getId).collect(Collectors.toList()))
                        .id(externalMovie.getId())
                        .original_language(externalMovie.getOriginal_language())
                        .original_title(externalMovie.getOriginal_title())
                        .overview(externalMovie.getOverview())
                        .popularity(externalMovie.getPopularity())
                        .poster_path(externalMovie.getPoster_path())
                        .release_date(externalMovie.getRelease_date())
                        .title(externalMovie.getTitle())
                        .video(externalMovie.getVideo())
                        .vote_average(externalMovie.getVote_average())
                        .vote_count(externalMovie.getVote_count())
                        .favourite(favourite)
                        .build();
            }
        }
        throw new ResourceNotFoundException("Favourite doesn't exists!");
    }
}