package me.toy.atdd.subwayservice.line.domain;

import me.toy.atdd.subwayservice.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @Transient
    private static final int MIN_SECTION_SIZE = 1;
    @Transient
    private static final int MIN_SIZE = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public List<Station> getStation() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }
        Station upStation = findUpStation();
        List<Station> orderedStations = orderedStations(upStation);
        return orderedStations;
    }

    private Station findUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isUpStation)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isUpStation(Station station) {
        return sections.stream()
                .noneMatch(section -> station == section.getDownStation());
    }

    private List<Station> orderedStations(Station baseStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(baseStation);

        List<Section> sections = new ArrayList<>(this.sections);
        Station nextStation = baseStation;

        while (sections.size() > MIN_SIZE) {
            Section section = nextStation(sections, nextStation);
            Station downStation = section.getDownStation();
            stations.add(downStation);
        }
        return stations;
    }

    private Section nextStation(List<Section> sections, Station nextStation) {
        Section nextSection = sections.stream()
                .filter(section -> nextStation == section.getUpStation())
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
        sections.remove(nextSection);
        return nextSection;
    }

    public List<Section> getSections() {
        return sections;
    }
}
