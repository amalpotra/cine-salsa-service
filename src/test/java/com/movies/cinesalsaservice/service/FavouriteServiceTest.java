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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FavouriteService.class)
public class FavouriteServiceTest {
    private final FavouriteService favouriteService;
    private final Favourite favourite = Favourite.builder()
            .id(1L)
            .contentType(ContentType.MOVIE)
            .contentId(12345L)
            .rating(8)
            .comments("A great movie to watch")
            .lastModified(LocalDateTime.now())
            .build();
    private final Favourite favourite1 = Favourite.builder()
            .id(2L)
            .contentType(ContentType.MOVIE)
            .contentId(67890L)
            .rating(6)
            .comments("Not a great movie to watch")
            .lastModified(LocalDateTime.now())
            .build();
    private final NewFavourite newFavourite = NewFavourite.builder()
            .contentType(ContentType.MOVIE)
            .contentId(12345L)
            .rating(8)
            .comments("A great movie to watch")
            .build();
    private final RevisedFavourite revisedFavourite = RevisedFavourite.builder()
            .rating(8)
            .comments("A great movie to watch")
            .build();
    private final ExternalMovie externalMovie = ExternalMovie.builder()
            .adult(false)
            .backdrop_path("/sample-path")
            .genres(List.of(Genre.builder()
                    .id(1)
                    .name("Action")
                    .build())
            )
            .id(12345L)
            .original_language("en")
            .original_title("A Movie")
            .overview("A nice description")
            .popularity(5011.11F)
            .poster_path("/sample-path")
            .release_date(LocalDate.of(2021, 3, 1))
            .title("Movie Name")
            .video(false)
            .vote_average(7.8F)
            .vote_count(5011L)
            .build();
    private final List<Movie> movieList = List.of(
            Movie.builder().id(12345L).build(),
            Movie.builder().id(12345L).build()
    );
    Long favouriteId = 1L;
    @MockBean
    private FavouriteRepository favouriteRepository;
    @MockBean
    private ExternalMovieService externalMovieService;

    @Autowired
    public FavouriteServiceTest(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @Test
    public void returnFavouriteOnSavingIt() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);
        Favourite returnedFavourite = favouriteService.newFavourite(newFavourite);

        assertAll("Should return same details",
                () -> assertEquals(favourite.getContentId(), returnedFavourite.getContentId()),
                () -> assertEquals(favourite.getContentType(), returnedFavourite.getContentType()),
                () -> assertEquals(favourite.getRating(), returnedFavourite.getRating()),
                () -> assertEquals(favourite.getComments(), returnedFavourite.getComments())
        );
    }

    @Test
    public void raiseResourceConflictExceptionOnDuplicateFavourite() {
        when(favouriteRepository.findFirstByContentTypeAndContentId(newFavourite.getContentType(), newFavourite.getContentId())).thenReturn(Optional.of(favourite));

        assertThrows(ResourceConflictException.class, () -> favouriteService.newFavourite(newFavourite));
    }

    @Test
    public void returnFavouriteOnRevisingIt() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);
        Favourite returnedFavourite = favouriteService.reviseFavourite(favouriteId, revisedFavourite);

        assertAll("Should return same details",
                () -> assertEquals(favouriteId, returnedFavourite.getId()),
                () -> assertEquals(revisedFavourite.getRating(), returnedFavourite.getRating()),
                () -> assertEquals(revisedFavourite.getComments(), returnedFavourite.getComments())
        );
    }

    @Test
    public void raiseResourceNotFoundExceptionOnRevisingNonExistingFavourite() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favouriteService.reviseFavourite(favouriteId, revisedFavourite));
    }

    @Test
    public void deleteFavouriteOnDeletingIt() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));

        favouriteService.deleteFavourite(favouriteId);
        verify(favouriteRepository, times(1)).deleteById(favouriteId);
    }

    @Test
    public void raiseResourceNotFoundExceptionOnDeletingNonExistingFavourite() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favouriteService.deleteFavourite(favouriteId));
    }

    @Test
    public void returnFavouriteMovieOnRequestingIt() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.of(favourite));
        when(externalMovieService.fetchMovie(favourite.getContentId())).thenReturn(externalMovie);

        Movie returnedMovie = favouriteService.getContent(favouriteId, favourite.getContentType());

        assertAll("Should return correct favourite movie",
                () -> assertEquals(favouriteId, returnedMovie.getFavourite().getId()),
                () -> assertEquals(favourite.getContentType(), returnedMovie.getFavourite().getContentType()),
                () -> assertEquals(favourite.getContentId(), returnedMovie.getId())
        );
    }

    @Test
    public void raiseResourceNotFoundExceptionOnRequestingNonExistingFavourite() {
        when(favouriteRepository.findById(favouriteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> favouriteService.getContent(favouriteId, favourite.getContentType()));
    }

    @Test
    public void returnAllAvailableFavouriteMovies() {
        when(favouriteRepository.findAll()).thenReturn(List.of(favourite, favourite1));
        when(externalMovieService.fetchMovie(argThat(contentId -> contentId.equals(favourite.getContentId()) || contentId.equals(favourite1.getContentId())))).thenReturn(externalMovie);

        List<Movie> returnedMovieList = favouriteService.getAllContent(favourite.getContentType());

        assertAll(
                () -> assertEquals(2, returnedMovieList.size()),
                () -> verify(externalMovieService, times(2)).fetchMovie(argThat(contentId -> contentId.equals(favourite.getContentId()) || contentId.equals(favourite1.getContentId())))
        );
    }
}