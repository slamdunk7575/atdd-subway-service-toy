package me.toy.atdd.subwayservice.station.application;

import me.toy.atdd.subwayservice.station.domain.Station;
import me.toy.atdd.subwayservice.station.domain.StationRepository;
import me.toy.atdd.subwayservice.station.dto.StationRequest;
import me.toy.atdd.subwayservice.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    public Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }
}
