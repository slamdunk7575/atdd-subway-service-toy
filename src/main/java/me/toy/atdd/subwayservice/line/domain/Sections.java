package me.toy.atdd.subwayservice.line.domain;

import me.toy.atdd.subwayservice.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if (!isExistedUpStation && !isExistedDownStation) {
            throw new IllegalArgumentException("등록할 수 없는 구간입니다.");
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

        Optional<Section> upSection = findByUpSection(station);
        Optional<Section> downSection = findByDownSection(station);
        validateRemoveStations(upSection.isPresent(), downSection.isPresent());

        if (canRemove(upSection.isPresent(), downSection.isPresent())) {
            removeMiddleStation(line, upSection.get(), downSection.get());
        }

        upSection.ifPresent(section -> sections.remove(section));
        downSection.ifPresent(section -> sections.remove(section));
    }

    private void validateRemoveStations(boolean isExistedUpStation, boolean isExistedDownStation) {
        if (!isExistedUpStation && !isExistedDownStation) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역은 제거할 수 없습니다.");
        }
    }

    private void validateRemoveSection() {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("구간이 하나인 지하철 노선은 구간을 제거할 수 없습니다.");
        }
    }

    private Optional<Section> findByUpSection(Station removeStation) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(removeStation))
                .findFirst();
    }

    private Optional<Section> findByDownSection(Station removeStation) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(removeStation))
                .findFirst();
    }

    public boolean canRemove(boolean upStation, boolean downStation) {
        return upStation && downStation;
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
