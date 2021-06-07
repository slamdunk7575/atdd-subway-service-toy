package study.unit;

import me.toy.atdd.subwayservice.line.application.LineService;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.LineRepository;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.station.application.StationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("단위 테스트 - Mockito의 MockitoExtension 활용한 가짜 협력 객체 사용")
@ExtendWith(MockitoExtension.class)
public class MockitoExtensionTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Test
    void findAllLines() {
        // given
        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(new Line()));
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        List<LineResponse> responses = lineService.findLines();

        // then
        assertThat(responses).hasSize(1);
    }

}
