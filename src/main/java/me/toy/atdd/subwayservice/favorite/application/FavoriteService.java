package me.toy.atdd.subwayservice.favorite.application;

import me.toy.atdd.subwayservice.auth.application.AuthorizationException;
import me.toy.atdd.subwayservice.auth.domain.LoginMember;
import me.toy.atdd.subwayservice.favorite.domain.Favorite;
import me.toy.atdd.subwayservice.favorite.domain.FavoriteRepository;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteRequest;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteResponse;
import me.toy.atdd.subwayservice.member.domain.Member;
import me.toy.atdd.subwayservice.member.domain.MemberRepository;
import me.toy.atdd.subwayservice.station.domain.Station;
import me.toy.atdd.subwayservice.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StationRepository stationRepository;
    private final MemberRepository memberRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, StationRepository stationRepository, MemberRepository memberRepository) {
        this.favoriteRepository = favoriteRepository;
        this.stationRepository = stationRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public FavoriteResponse createFavorite(LoginMember loginMember, FavoriteRequest favoriteRequest) {
        validateCreateFavorite(favoriteRequest);
        Favorite persistFavorite = favoriteRepository.save(createFavoriteEntity(loginMember, favoriteRequest));
        return FavoriteResponse.of(persistFavorite);
    }

    private Favorite createFavoriteEntity(LoginMember loginMember, FavoriteRequest favoriteRequest) {
        Member member = getMember(loginMember);

        List<Station> findResult = findAllByIdIn(favoriteRequest);

        Station source = getStation(findResult, favoriteRequest.getSource());
        Station target = getStation(findResult, favoriteRequest.getTarget());

        return new Favorite(member, source, target);
    }

    private Member getMember(LoginMember loginMember) {
        return memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new AuthorizationException("만료된 ID 입니다."));
    }

    private List<Station> findAllByIdIn(FavoriteRequest favoriteRequest) {
        return stationRepository.findAllByIdIn(
                Arrays.asList(favoriteRequest.getSource(), favoriteRequest.getTarget()));
    }

    private Station getStation(List<Station> findResult, Long stationId) {
        return findResult.stream()
                .filter(station -> station.isSameStation(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출발역 입니다."));
    }

    private void validateCreateFavorite(FavoriteRequest favoriteRequest) {
        if (Objects.equals(favoriteRequest.getSource(), favoriteRequest.getTarget())) {
            throw new IllegalArgumentException("즐겨찾기의 출발역과 도착역은 같을 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavorites(LoginMember loginMember) {
        List<Favorite> favorites = favoriteRepository.findAllByMemberId(loginMember.getId());
        return FavoriteResponse.ofList(favorites);
    }
}
