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
import java.util.List;
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
            Favourite favourite = new Favourite();
            favourite.setContentType(newFavourite.getContentType());
            favourite.setContentId(newFavourite.getContentId());
            favourite.setRating(newFavourite.getRating());
            favourite.setComments(newFavourite.getComments());
            favourite.setLastModified(LocalDateTime.now());

            return favouriteRepository.save(favourite);
        }
        throw new ResourceConflictException("Favourite already exists!");
    }

    public Favourite reviseFavourite(Long favouriteId, RevisedFavourite revisedFavourite) {
        Optional<Favourite> optionalFavourite = favouriteRepository.findById(favouriteId);
        if (optionalFavourite.isPresent()) {
            Favourite favourite = optionalFavourite.get();
            favourite.setRating(revisedFavourite.getRating());
            favourite.setComments(revisedFavourite.getComments());
            favourite.setLastModified(LocalDateTime.now());

            return favouriteRepository.save(favourite);
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

        return optionalFavourite.map(favourite -> {
            if (contentType == ContentType.MOVIE) {
                Movie movie = new Movie();
                ExternalMovie externalMovie = externalMovieService.fetchMovie(favourite.getContentId());

                movie.setAdult(externalMovie.getAdult());
                movie.setBackdrop_path(externalMovie.getBackdrop_path());
                movie.setGenre_ids(externalMovie.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
                movie.setId(externalMovie.getId());
                movie.setOriginal_language(externalMovie.getOriginal_language());
                movie.setOriginal_title(externalMovie.getOriginal_title());
                movie.setOverview(externalMovie.getOverview());
                movie.setPopularity(externalMovie.getPopularity());
                movie.setPoster_path(externalMovie.getPoster_path());
                movie.setRelease_date(externalMovie.getRelease_date());
                movie.setTitle(externalMovie.getTitle());
                movie.setVideo(externalMovie.getVideo());
                movie.setVote_average(externalMovie.getVote_average());
                movie.setVote_count(externalMovie.getVote_count());
                movie.setFavourite(favourite);

                return movie;
            } else
                throw new UnsupportedOperationException("Unknown content type!");
        }).orElseThrow(() -> new ResourceNotFoundException("Favourite doesn't exists!"));
    }

    public List<Movie> getAllContent(ContentType contentType) {
        List<Favourite> favouriteList = favouriteRepository.findAll();

        return favouriteList.stream().map(favourite -> {
            ExternalMovie externalMovie = externalMovieService.fetchMovie(favourite.getContentId());

            Movie movie = new Movie();
            movie.setAdult(externalMovie.getAdult());
            movie.setBackdrop_path(externalMovie.getBackdrop_path());
            movie.setGenre_ids(externalMovie.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
            movie.setId(externalMovie.getId());
            movie.setOriginal_language(externalMovie.getOriginal_language());
            movie.setOriginal_title(externalMovie.getOriginal_title());
            movie.setOverview(externalMovie.getOverview());
            movie.setPopularity(externalMovie.getPopularity());
            movie.setPoster_path(externalMovie.getPoster_path());
            movie.setRelease_date(externalMovie.getRelease_date());
            movie.setTitle(externalMovie.getTitle());
            movie.setVideo(externalMovie.getVideo());
            movie.setVote_average(externalMovie.getVote_average());
            movie.setVote_count(externalMovie.getVote_count());
            movie.setFavourite(favourite);

            return movie;
        }).collect(Collectors.toList());
    }
}