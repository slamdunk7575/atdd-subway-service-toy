package me.toy.atdd.subwayservice.line.domain;

import me.toy.atdd.subwayservice.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Station> getStations() {
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
            nextStation = downStation;
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

    public void addSection(Section newSection) {
        boolean isExistedUpStation = containStation(newSection.getUpStation());
        boolean isExistedDownStation = containStation(newSection.getDownStation());

        validateAddSection(isExistedUpStation, isExistedDownStation);

        if (isExistedUpStation) {
            changeExistingUpStation(newSection);
        }

        if (isExistedDownStation) {
            changeExistingDownStation(newSection);
        }

        sections.add(newSection);
    }

    private void validateAddSection(boolean isExistedUpStation, boolean isExistedDownStation) {
        if (isExistedUpStation && isExistedDownStation) {
            throw new IllegalArgumentException("?????? ????????? ???????????????.");
        }

        if (!isExistedUpStation && !isExistedDownStation) {
            throw new IllegalArgumentException("????????? ??? ?????? ???????????????.");
        }
    }

    private boolean containStation(Station station) {
        return getStations().stream()
                .anyMatch(it -> it == station);
    }

    public void changeExistingUpStation(Section newSection) {
        sections.stream()
                .filter(it -> it.getUpStation() == newSection.getUpStation())
                .findFirst()
                .ifPresent(it -> it.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    private void changeExistingDownStation(Section newSection) {
        sections.stream()
                .filter(it -> it.getDownStation() == newSection.getDownStation())
                .findFirst()
                .ifPresent(it -> it.updateDownStation(newSection.getUpStation(), newSection.getDistance()));
    }

    public void removeSection(Line line, Station station) {
        validateRemoveSection();

        Section upSection = findSectionByUpSection(station);
        Section downSection = findSectionByDownSection(station);

        if (Objects.nonNull(upSection) && Objects.nonNull(downSection)) {
            removeMiddleStation(line, upSection, downSection);
        }

        validateRemoveStations(upSection, downSection);

        sections.remove(upSection);
        sections.remove(downSection);
    }

    private void validateRemoveStations(Section upSection, Section downSection) {
        if (Objects.isNull(upSection) && Objects.isNull(downSection)) {
            throw new IllegalArgumentException("????????? ???????????? ?????? ?????? ????????? ??? ????????????.");
        }
    }

    private void validateRemoveSection() {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("????????? ????????? ????????? ????????? ????????? ????????? ??? ????????????.");
        }
    }

    private Section findSectionByUpSection(Station removeStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(removeStation))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionByDownSection(Station removeStation) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(removeStation))
                .findFirst()
                .orElse(null);
    }

    private void removeMiddleStation(Line line, Section upSection, Section downSection) {
        Station newUpStation = downSection.getUpStation();
        Station newDownStation = upSection.getDownStation();
        Distance newDistance = upSection.getDistance().addDistance(downSection.getDistance());
        sections.add(Section.builder()
                .line(line)
                .upStation(newUpStation)
                .downStation(newDownStation)
                .distance(newDistance)
                .build());
    }

}
