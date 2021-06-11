package study.unit;

import me.toy.atdd.subwayservice.line.application.LineService;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.LineRepository;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.station.application.StationService;
import me.toy.atdd.subwayservice.station.domain.Station;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("단위 테스트 - SpringExtension 활용한 가짜 협력 객 사용")
@ExtendWith(SpringExtension.class)
public class SpringExtensionTest {

    @MockBean
    private LineRepository lineRepository;

    @MockBean
    private StationService stationService;

    @Test
    void findAllLines() {
        // given
        Line line = Line.builder()
                .name("팔호선")
                .color("pink")
                .upStation(new Station("천호역"))
                .downStation(new Station("산성역"))
                .distance(30)
                .build();

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line));
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        List<LineResponse> responses = lineService.findLines();

        // then
        assertThat(responses).hasSize(1);
    }

}
