package me.toy.atdd.subwayservice.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
}
