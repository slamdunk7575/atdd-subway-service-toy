package me.toy.atdd.subwayservice.favorite.dto;

import lombok.Builder;

public class FavoriteRequest {
    private Long source;
    private Long target;

    private FavoriteRequest() {
    }

    @Builder
    public FavoriteRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}
