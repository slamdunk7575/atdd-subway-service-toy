package me.toy.atdd.subwayservice.favorite.application;

public class FavoriteValidationException extends RuntimeException {
    public FavoriteValidationException() {
    }

    public FavoriteValidationException(String message) {
        super(message);
    }
}
