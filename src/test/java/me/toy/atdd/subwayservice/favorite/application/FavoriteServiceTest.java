package me.toy.atdd.subwayservice.favorite.application;

import me.toy.atdd.subwayservice.auth.domain.LoginMember;
import me.toy.atdd.subwayservice.favorite.domain.Favorite;
import me.toy.atdd.subwayservice.favorite.domain.FavoriteRepository;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteRequest;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteResponse;
import me.toy.atdd.subwayservice.member.domain.Member;
import me.toy.atdd.subwayservice.member.domain.MemberRepository;
import me.toy.atdd.subwayservice.station.domain.Station;
import me.toy.atdd.subwayservice.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("즐겨찾기 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private Member 사용자;
    private Station 천호역;
    private Station 잠실역;
    private Favorite 즐겨찾기;

    @BeforeEach
    void setUp() {
        사용자 = makeMockMember(1L, "slamdunk7575@test.com",18);
        천호역 = makeMockStation(1L, "천호역");
        잠실역 = makeMockStation(2L, "잠실역");
        즐겨찾기 = makeMockFavorite(1L, 천호역, 잠실역);
    }

    @DisplayName("즐겨찾기 생성")
    @Test
    void createFavorite() {
        given(memberRepository.findById(any())).willReturn(Optional.of(사용자));
        given(stationRepository.findAllByIdIn(anyList())).willReturn(Arrays.asList(천호역, 잠실역));
        given(favoriteRepository.save(any())).willReturn(즐겨찾기);

        LoginMember loginMember = new LoginMember(사용자.getId(), 사용자.getEmail(), 사용자.getAge());
        FavoriteRequest favoriteRequest = new FavoriteRequest(천호역.getId(), 잠실역.getId());
        FavoriteResponse favoriteResponse = favoriteService.createFavorite(loginMember, favoriteRequest);

        assertThat(favoriteResponse.getId()).isNotNull();
        assertThat(favoriteResponse.getSource().getId()).isEqualTo(천호역.getId());
        assertThat(favoriteResponse.getTarget().getId()).isEqualTo(잠실역.getId());
    }

    private Member makeMockMember(long id, String email, int age) {
        Member member = mock(Member.class);
        given(member.getId()).willReturn(id);
        given(member.getEmail()).willReturn(email);
        given(member.getAge()).willReturn(age);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Station makeMockStation(long id, String name) {
        Station station = mock(Station.class);
        given(station.getId()).willReturn(id);
        given(station.getName()).willReturn(name);
        ReflectionTestUtils.setField(station, "id", id);
        given(station.isSameStation(anyLong())).willCallRealMethod();
        return station;
    }

    private Favorite makeMockFavorite(long id, Station source, Station target) {
        Favorite favorite = mock(Favorite.class);
        given(favorite.getId()).willReturn(id);
        given(favorite.getSource()).willReturn(source);
        given(favorite.getTarget()).willReturn(target);
        return favorite;
    }

}
