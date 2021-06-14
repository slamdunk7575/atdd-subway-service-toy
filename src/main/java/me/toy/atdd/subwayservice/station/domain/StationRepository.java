package me.toy.atdd.subwayservice.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findAllByIdIn(List<Long> ids);
}
