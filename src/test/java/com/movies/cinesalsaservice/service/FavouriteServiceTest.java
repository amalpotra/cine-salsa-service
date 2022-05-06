package com.movies.cinesalsaservice.service;

import com.movies.cinesalsaservice.constant.ContentType;
import com.movies.cinesalsaservice.exception.ResourceConflictException;
import com.movies.cinesalsaservice.exception.ResourceNotFoundException;
import com.movies.cinesalsaservice.model.Favourite;
import com.movies.cinesalsaservice.model.view.NewFavourite;
import com.movies.cinesalsaservice.model.view.RevisedFavourite;
import com.movies.cinesalsaservice.repository.FavouriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = FavouriteService.class)
public class FavouriteServiceTest {
    @MockBean
    private FavouriteRepository favouriteRepository;
    @Autowired
    private FavouriteService favouriteService;

    Favourite favourite = Favourite.builder()
            .id(1L)
            .contentType(ContentType.MOVIE)
            .contentId(12345L)
            .rating(8)
            .comments("A great movie to watch")
            .lastModified(LocalDateTime.now())
            .build();
    NewFavourite newFavourite = NewFavourite.builder()
            .contentType(ContentType.MOVIE)
            .contentId(12345L)
            .rating(8)
            .comments("A great movie to watch")
            .build();
    RevisedFavourite revisedFavourite = RevisedFavourite.builder()
            .rating(8)
            .comments("A great movie to watch")
            .build();
    Long favouriteId = 1L;

    @Test
    public void returnFavouriteOnSavingIt() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);
        Favourite returnedFavourite = favouriteService.newFavourite(newFavourite);

        assertEquals(favourite.getContentId(), returnedFavourite.getContentId());
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

        assertEquals(favouriteId, returnedFavourite.getId());
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
}