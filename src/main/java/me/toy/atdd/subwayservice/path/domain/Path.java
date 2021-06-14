package me.toy.atdd.subwayservice.path.domain;

import me.toy.atdd.subwayservice.line.domain.Line;
import me.toy.atdd.subwayservice.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private GraphPath<Station, DefaultWeightedEdge> path;

    private Path(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initGraph(lines);
    }

    public static Path of(List<Line> lines) {
        return new Path(lines);
    }

    private void initGraph(List<Line> lines) {
        for (Line line : lines) {
            addLineToGraph(graph, line);
        }
    }

    private void addLineToGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        line.getSections().forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistanceWeight());
        });
    }

    public GraphPath<Station, DefaultWeightedEdge> findShortestPath(Station source, Station target) {
        validateSection(source, target);
        try {
            return new DijkstraShortestPath<>(graph).getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("경로에 포함되어 있지 않은 역입니다.");
        }
    }

    private void validateSection(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException("조회하려는 출발지와 도착지가 같습니다.");
        }
    }

}
