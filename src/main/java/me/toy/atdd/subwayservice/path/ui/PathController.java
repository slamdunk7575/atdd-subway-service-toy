package me.toy.atdd.subwayservice.path.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toy.atdd.subwayservice.path.application.PathService;
import me.toy.atdd.subwayservice.path.dto.PathRequest;
import me.toy.atdd.subwayservice.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@RequestBody PathRequest pathRequest) {
        return ResponseEntity.ok(pathService.findShortestPath(pathRequest));
    }
}
