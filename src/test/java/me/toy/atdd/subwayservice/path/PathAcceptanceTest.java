package me.toy.atdd.subwayservice.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayservice.AcceptanceTest;
import me.toy.atdd.subwayservice.line.dto.LineRequest;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.path.dto.PathResponse;
import me.toy.atdd.subwayservice.station.StationFixtures;
import me.toy.atdd.subwayservice.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static me.toy.atdd.subwayservice.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static me.toy.atdd.subwayservice.line.LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청;
import static me.toy.atdd.subwayservice.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 팔호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 몽촌토성역;
    private StationResponse 잠실역;

    /**           거리 10
     * 교대역  --- *2호선* ---  강남역
     * |                       |
     * *3호선*                *신분당선*
     거리 3                    거리 10
     * |                       |
     * 남부터미널역 --- *3호선* --- 양재역
     *               거리 2
     */

    /**              거리 2
     * 몽촌토성역  --- *8호선* --- 잠실역
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        몽촌토성역 = 지하철역_등록되어_있음("몽촌토성역").as(StationResponse.class);
        잠실역 = 지하철역_등록되어_있음("잠실역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-green-600", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(new LineRequest("삼호선", "bg-orange-600", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
        팔호선 = 지하철_노선_등록되어_있음(new LineRequest("팔호선", "bg-pink-600", 몽촌토성역.getId(), 잠실역.getId(), 2)).as(LineResponse.class);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("지하철 구간의 최단 경로를 찾음")
    @Test
    void findShortestPathSuccess() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(강남역.getId(), 남부터미널역.getId());

        // then
        지하철_최단_경로_조회됨(response, Arrays.asList("강남역", "양재역", "남부터미널역"));

    }

    @DisplayName("노선 미포함 역으로 경로 조회 실패함")
    @Test
    void findShortestPathFail() {
        // when
        ExtractableResponse<Response> 노선_미포함_경로_조회 = 지하철_경로_조회_요청(강남역.getId(), StationFixtures.천호역.getId());

        // then
        지하철_경로_조회_실패함(노선_미포함_경로_조회);
    }

    public ExtractableResponse<Response> 지하철_경로_조회_요청(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", source)
                .queryParam("target", target)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    public void 지하철_최단_경로_조회됨(ExtractableResponse<Response> response, List<String> expectedStations) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<StationResponse> stationResponses = response.body().as(PathResponse.class).getStations();
        assertThat(stationResponses)
                .map(StationResponse::getName)
                .asList()
                .containsExactlyElementsOf(expectedStations);
    }

    public void 지하철_경로_조회_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
