package me.toy.atdd.subwayservice.path.application;

import lombok.RequiredArgsConstructor;
import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.line.domain.LineRepository;
import me.toy.atdd.subwayservice.path.domain.Path;
import me.toy.atdd.subwayservice.path.dto.PathRequest;
import me.toy.atdd.subwayservice.path.dto.PathResponse;
import me.toy.atdd.subwayservice.station.domain.Station;
import me.toy.atdd.subwayservice.station.domain.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathResponse findShortestPath(PathRequest pathRequest) {
        List<Station> findResult = findAllByIdIn(pathRequest);

        Station sourceStation = getStation(findResult, pathRequest.getSourceStationId());
        Station targetStation = getStation(findResult, pathRequest.getTargetStationId());

        List<Line> lines = lineRepository.findAll();
        Path path = Path.of(lines);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = path.findShortestPath(sourceStation, targetStation);
        return PathResponse.of(shortestPath.getVertexList(), (int) shortestPath.getWeight());
    }

    private Station getStation(List<Station> findResult, Long stationId) {
        return findResult.stream()
                .filter(station -> station.isSameStation(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역입니다."));
    }

    private List<Station> findAllByIdIn(PathRequest pathRequest) {
        return stationRepository.findAllByIdIn(Arrays.asList(pathRequest.getSourceStationId(),
                        pathRequest.getTargetStationId()));
    }




}
