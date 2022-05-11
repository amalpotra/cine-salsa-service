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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
    private static final Favourite favourite = new Favourite();
    private static final Favourite favourite1 = new Favourite();
    private static final NewFavourite newFavourite = new NewFavourite();
    private static final RevisedFavourite revisedFavourite = new RevisedFavourite();
    private static final ExternalMovie externalMovie = new ExternalMovie();
    private static final ExternalMovie externalMovie1 = new ExternalMovie();
    private static Long favouriteId;
    private final FavouriteService favouriteService;
    @MockBean
    private FavouriteRepository favouriteRepository;
    @MockBean
    private ExternalMovieService externalMovieService;

    @Autowired
    public FavouriteServiceTest(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    // Utilities

    @BeforeAll
    static void init() {
        setupFavourite(favourite, 1L, ContentType.MOVIE, 12345L, 8, "A great movie to watch", LocalDateTime.now());
        setupFavourite(favourite1, 2L, ContentType.MOVIE, 67890L, 6, "Not a great movie to watch", LocalDateTime.now());
        setupNewFavourite();
        setupRevisedFavourite();
        setupExternalMovie();
        favouriteId = 1L;
    }

    private static void setupExternalMovie() {
        externalMovie.setAdult(false);
        externalMovie.setBackdrop_path("/sample-path");
        externalMovie.setGenres(List.of(new Genre(1, "Action")));
        externalMovie.setId(12345L);
        externalMovie.setOriginal_language("en");
        externalMovie.setOriginal_title("A Movie");
        externalMovie.setOverview("A nice description");
        externalMovie.setPopularity(5011.11F);
        externalMovie.setPoster_path("/sample-path");
        externalMovie.setRelease_date(LocalDate.of(2021, 3, 1));
        externalMovie.setTitle("Movie Name");
        externalMovie.setVideo(false);
        externalMovie.setVote_average(7.8F);
        externalMovie.setVote_count(5011L);

        externalMovie1.setBackdrop_path("/sample-path");
        externalMovie1.setGenres(List.of(new Genre(2, "Drama")));
        externalMovie1.setId(67890L);
        externalMovie1.setOriginal_language("FR");
        externalMovie1.setOriginal_title("Another Movie");
        externalMovie1.setOverview("Another nice description");
        externalMovie1.setPopularity(3011.11F);
        externalMovie1.setPoster_path("/sample-path");
        externalMovie1.setRelease_date(LocalDate.of(2021, 7, 11));
        externalMovie1.setTitle("Another Movie Name");
        externalMovie1.setVideo(false);
        externalMovie1.setVote_average(4.8F);
        externalMovie1.setVote_count(3011L);
    }

    private static void setupRevisedFavourite() {
        revisedFavourite.setRating(8);
        revisedFavourite.setComments("A great movie to watch");
    }

    private static void setupNewFavourite() {
        newFavourite.setContentType(ContentType.MOVIE);
        newFavourite.setContentId(12345L);
        newFavourite.setRating(8);
        newFavourite.setComments("A great movie to watch");
    }

    private static void setupFavourite(Favourite favourite, long id, ContentType contentType, long contentId, int rating, String comments, LocalDateTime dateTime) {
        favourite.setId(id);
        favourite.setContentType(contentType);
        favourite.setContentId(contentId);
        favourite.setRating(rating);
        favourite.setComments(comments);
        favourite.setLastModified(dateTime);
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
        when(externalMovieService.fetchMovie(favourite.getContentId())).thenReturn(externalMovie);
        when(externalMovieService.fetchMovie(favourite1.getContentId())).thenReturn(externalMovie1);

        List<Movie> returnedMovieList = favouriteService.getAllContent(favourite.getContentType());

        assertAll(
                () -> assertEquals(2, returnedMovieList.size()),
                () -> verify(externalMovieService, times(2)).fetchMovie(argThat(contentId -> contentId.equals(favourite.getContentId()) || contentId.equals(favourite1.getContentId()))),
                () -> assertEquals(favourite.getId(), returnedMovieList.get(0).getFavourite().getId()),
                () -> assertEquals(favourite1.getId(), returnedMovieList.get(1).getFavourite().getId()),
                () -> assertEquals(favourite.getContentId(), returnedMovieList.get(0).getId()),
                () -> assertEquals(favourite1.getContentId(), returnedMovieList.get(1).getId()),
                () -> assertEquals(externalMovie.getTitle(), returnedMovieList.get(0).getTitle()),
                () -> assertEquals(externalMovie1.getTitle(), returnedMovieList.get(1).getTitle())
        );
    }
}