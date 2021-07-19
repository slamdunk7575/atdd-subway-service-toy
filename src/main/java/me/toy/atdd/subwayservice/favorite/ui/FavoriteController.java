package me.toy.atdd.subwayservice.favorite.ui;

import me.toy.atdd.subwayservice.auth.domain.AuthenticationPrincipal;
import me.toy.atdd.subwayservice.auth.domain.LoginMember;
import me.toy.atdd.subwayservice.favorite.application.FavoriteService;
import me.toy.atdd.subwayservice.favorite.domain.Favorite;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteRequest;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> createFavorite(@AuthenticationPrincipal LoginMember loginMember,
                                                           @RequestBody FavoriteRequest favoriteRequest) {
        FavoriteResponse favoriteResponse = favoriteService.createFavorite(loginMember, favoriteRequest);
        return ResponseEntity.created(URI.create("/favorites/" + favoriteResponse.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok(favoriteService.getFavorites(loginMember));
    }



}
