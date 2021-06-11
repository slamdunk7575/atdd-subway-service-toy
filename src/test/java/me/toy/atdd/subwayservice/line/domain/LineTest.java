package me.toy.atdd.subwayservice.line.domain;

import me.toy.atdd.subwayservice.station.StationFixtures;
import me.toy.atdd.subwayservice.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    private Line 팔호선;

    @BeforeEach
    void setUp() {
        팔호선 = Line.builder()
                .name("팔호선")
                .color("pink")
                .upStation(StationFixtures.천호역)
                .downStation(StationFixtures.산성역)
                .distance(30)
                .build();

        Section 천호역_잠실역 = Section.builder().line(팔호선)
                .upStation(StationFixtures.천호역)
                .downStation(StationFixtures.잠실역)
                .distance(new Distance(10))
                .build();

        팔호선.add(천호역_잠실역);
    }

    @DisplayName("지하철 라인에 구간을 등록")
    @Test
    void addSectionInLine() {
        // given
        Section newSection = Section.builder()
                .line(팔호선)
                .upStation(StationFixtures.잠실역)
                .downStation(StationFixtures.문정역)
                .distance(new Distance(5))
                .build();

        // when
        팔호선.add(newSection);

        // then
        assertThat(팔호선.getStations()).containsExactlyElementsOf(Arrays.asList(
                StationFixtures.천호역, StationFixtures.잠실역,
                StationFixtures.문정역, StationFixtures.산성역));

        assertThat(getTotalDistance()).isEqualTo(30);
    }

    @DisplayName("지하철 라인의 구간을 삭제")
    @Test
    void removeSectionInLine() {
        // given & when
        팔호선.remove(StationFixtures.잠실역);

        // then
        assertThat(팔호선.getStations()).containsExactlyElementsOf(Arrays.asList(StationFixtures.천호역, StationFixtures.산성역));
        assertThat(getTotalDistance()).isEqualTo(30);
    }

    private int getTotalDistance() {
        int totalDistance = 팔호선.getSections().stream()
                .map(section -> section.getDistance())
                .mapToInt(Distance::get)
                .sum();
        return totalDistance;
    }

}
