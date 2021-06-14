package me.toy.atdd.subwayservice.path.domain;

import me.toy.atdd.subwayservice.line.domain.Distance;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.Section;
import me.toy.atdd.subwayservice.station.StationFixtures;
import me.toy.atdd.subwayservice.station.domain.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathTest {

    /**
     *              거리 10
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * 거리 3                     거리 10
     * |                        |
     * 남부터미널역  --- *3호선* --- 양재역
     *              거리 2
     */

    public static Path path;

    @BeforeAll
    void setUp() {
        Line 이호선 = new Line("이호선", "bg-green-600", StationFixtures.교대역, StationFixtures.강남역, 10);
        Line 삼호선 = new Line("삼호선", "bg-orange-600", StationFixtures.교대역, StationFixtures.남부터미널역, 5);
        Line 신분당선 = new Line("신분당선", "bg-red-600", StationFixtures.강남역, StationFixtures.양재역, 10);

        Section 남부터미널_양재역 = Section.builder().line(삼호선)
                .upStation(StationFixtures.남부터미널역)
                .downStation(StationFixtures.양재역)
                .distance(new Distance(2))
                .build();
        삼호선.add(남부터미널_양재역);

        path = Path.of(Arrays.asList(이호선, 삼호선, 신분당선));
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestPath() {
        // given
        List<Station> stations = path.findShortestPath(StationFixtures.강남역, StationFixtures.남부터미널역).getVertexList();

        // when
        assertThat(stations)
                .map(Station::getName)
                .containsExactly("강남역", "양재역", "남부터미널역");
    }

    @DisplayName("출발지와 도착지가 서로 동일한 경우")
    @Test
    void findPathSameStations() {
        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            path.findShortestPath(StationFixtures.강남역, StationFixtures.강남역);
        }).withMessageMatching("조회하려는 출발지와 도착지가 같습니다.");
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경우")
    @Test
    void findNotExistStations() {
        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            path.findShortestPath(StationFixtures.강남역, StationFixtures.천호역);
        }).withMessageMatching("경로에 포함되어 있지 않은 역입니다.");
    }

}
