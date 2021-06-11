package me.toy.atdd.subwayservice.line.application;

import me.toy.atdd.subwayservice.line.domain.Distance;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.LineRepository;
import me.toy.atdd.subwayservice.line.domain.Section;
import me.toy.atdd.subwayservice.line.dto.LineRequest;
import me.toy.atdd.subwayservice.line.dto.LineResponse;
import me.toy.atdd.subwayservice.line.dto.SectionRequest;
import me.toy.atdd.subwayservice.station.application.StationService;
import me.toy.atdd.subwayservice.station.domain.Station;
import me.toy.atdd.subwayservice.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLines() {
        List<Line> persistLines = lineRepository.findAll();
        return Line.ofList(persistLines);
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("지하철 노선이 존재하지 않습니다."));
    }

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        return LineResponse.of(persistLine);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line persistLine = findLineById(id);
        persistLine.update(lineUpdateRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addLineStation(Long lineId, SectionRequest request) {
        Line line = findLineById(lineId);
        Section section = toSection(line, request);
        line.add(section);
    }

    private Section toSection(Line line, SectionRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(request.getDistance()))
                .build();
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findStationById(stationId);
        line.remove(station);
    }
}
