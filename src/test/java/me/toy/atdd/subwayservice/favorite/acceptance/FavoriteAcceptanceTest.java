package me.toy.atdd.subwayservice.favorite.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayservice.AcceptanceTest;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteRequest;
import me.toy.atdd.subwayservice.favorite.dto.FavoriteResponse;
import me.toy.atdd.subwayservice.line.dto.LineRequest;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static me.toy.atdd.subwayservice.line.acceptance.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static me.toy.atdd.subwayservice.line.acceptance.LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청;
import static me.toy.atdd.subwayservice.member.MemberAcceptanceTest.회원_로그인_요청;
import static me.toy.atdd.subwayservice.member.MemberAcceptanceTest.회원_생성을_요청;
import static me.toy.atdd.subwayservice.station.acceptance.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {

    private LineResponse 팔호선;
    private StationResponse 천호역;
    private StationResponse 잠실역;
    private StationResponse 문정역;
    private String accessTokenMember1;
    private String accessTokenMember2;


    @BeforeAll
    public void setUp() {
        super.setUp();

        천호역 = 지하철역_등록되어_있음("천호역").as(StationResponse.class);
        잠실역 = 지하철역_등록되어_있음("잠실역").as(StationResponse.class);
        문정역 = 지하철역_등록되어_있음("문정역").as(StationResponse.class);

        팔호선 = 지하철_노선_등록되어_있음(new LineRequest("팔호선", "bg-pink-600",
                천호역.getId(), 문정역.getId(), 10)).as(LineResponse.class);

        지하철_노선에_지하철역_등록_요청(팔호선, 천호역, 잠실역, 5);

        회원_생성을_요청("member1@email.com", "1a2b3c", 21);
        accessTokenMember1 = 회원_로그인_요청("member1@email.com", "1a2b3c");

        회원_생성을_요청("member2@email.com", "3d4e5f", 24);
        accessTokenMember2 = 회원_로그인_요청("member2@email.com", "3d4e5f");
    }

    @DisplayName("즐겨찾기 관리")
    @Test
    void manageFavorite() {
        // 즐겨찾기 생성
        ExtractableResponse<Response> 즐겨찾기_천호역_잠실역_생성_결과 = 즐겨찾기_생성_요청(accessTokenMember1, 천호역, 잠실역);
        즐겨찾기_생성됨(즐겨찾기_천호역_잠실역_생성_결과);

        ExtractableResponse<Response> 즐겨찾기_잠실역_문정역_생성_결과 = 즐겨찾기_생성_요청(accessTokenMember1, 잠실역, 문정역);
        즐겨찾기_생성됨(즐겨찾기_잠실역_문정역_생성_결과);

        // 즐겨찾기 조회
        ExtractableResponse<Response> 즐겨찾기_목록_조회_요청_결과 = 즐겨찾기_목록_조회_요청(accessTokenMember1);
        즐겨찾기_목록_확인(즐겨찾기_목록_조회_요청_결과, Arrays.asList("천호역", "잠실역"));


        // 즐겨찾기 삭제
        ExtractableResponse<Response> 즐겨찾기_삭제_요청_결과 = 즐겨찾기_삭제_요청(accessTokenMember1, 즐겨찾기_천호역_잠실역_생성_결과);
        즐겨찾기_삭제됨(즐겨찾기_삭제_요청_결과);
        즐겨찾기_목록_확인(즐겨찾기_목록_조회_요청(accessTokenMember1), Arrays.asList("잠실역"));
    }

    public static ExtractableResponse<Response> 즐겨찾기_생성_요청(String accessToken, StationResponse sourceStation, StationResponse targetStation) {
        FavoriteRequest favoriteRequest = FavoriteRequest.builder()
                .source(sourceStation.getId())
                .target(targetStation.getId())
                .build();

        return RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(favoriteRequest)
                .when().post("/favorites")
                .then().log().all()
                .extract();
    }

    public static void 즐겨찾기_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 즐겨찾기_목록_조회_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(accessToken)
                .when().get("/favorites")
                .then().log().all()
                .extract();
    }

    public static void 즐겨찾기_목록_확인(ExtractableResponse<Response> 즐겨찾기_목록_조회_요청_결과, List<String> favoriteSources) {
        List<FavoriteResponse> favoriteResponses = 즐겨찾기_목록_조회_요청_결과.body().jsonPath().getList("", FavoriteResponse.class);

        assertThat(favoriteResponses)
                .map(favoriteResponse -> favoriteResponse.getSource().getName())
                .asList()
                .containsExactly(favoriteSources.toArray());
    }

    public static ExtractableResponse<Response> 즐겨찾기_삭제_요청(String accessToken, ExtractableResponse<Response> 즐겨찾기_생성_요청_결과) {
        String uri = 즐겨찾기_생성_요청_결과.header("Location");

        return RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 즐겨찾기_삭제됨(ExtractableResponse<Response> 즐겨찾기_삭제_요청_결과) {
        assertThat(즐겨찾기_삭제_요청_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
