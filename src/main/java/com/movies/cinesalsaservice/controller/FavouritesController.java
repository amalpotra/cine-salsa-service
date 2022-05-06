package com.movies.cinesalsaservice.controller;

import com.movies.cinesalsaservice.constant.ContentType;
import com.movies.cinesalsaservice.model.Favourite;
import com.movies.cinesalsaservice.model.view.NewFavourite;
import com.movies.cinesalsaservice.model.view.RevisedFavourite;
import com.movies.cinesalsaservice.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/v1/favourites")
public class FavouritesController {
    @Autowired
    private FavouriteService favouriteService;

    @GetMapping
    public String getAllFavourites(@RequestParam(name = "type") ContentType contentType) {
        return "All Favourites with type: " + contentType;
    }

    @GetMapping("/{favouriteId}")
    public String getFavourite(@PathVariable long favouriteId) {
        return "Favourite: " + favouriteId;
    }

    @PostMapping
    public ResponseEntity<Favourite> newFavourite(@RequestBody NewFavourite newFavourite) {
        Favourite favourite = favouriteService.newFavourite(newFavourite);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(favourite.getId())
                                .toUri()
                ).body(favourite);
    }

    @PutMapping("/{favouriteId}")
    public ResponseEntity<Favourite> reviseFavourite(@PathVariable long favouriteId, @RequestBody RevisedFavourite revisedFavourite) {
        return ResponseEntity.ok(favouriteService.reviseFavourite(favouriteId, revisedFavourite));
    }

    @DeleteMapping("/{favouriteId}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable long favouriteId) {
        favouriteService.deleteFavourite(favouriteId);
        return ResponseEntity.noContent().build();
    }
}
