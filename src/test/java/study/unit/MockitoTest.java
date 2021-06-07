package study.unit;

import me.toy.atdd.subwayservice.line.application.LineService;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.LineRepository;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.station.application.StationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("단위 테스트 - Mockito 활용 가짜 협력 객체 사용")
public class MockitoTest {

    @Test
    void findAllLines() {
        // given
        LineRepository lineRepository = mock(LineRepository.class);
        StationService stationService = mock(StationService.class);

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(new Line()));
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        List<LineResponse> responses = lineService.findLines();

        // then
        assertThat(responses).hasSize(1);
    }
}
