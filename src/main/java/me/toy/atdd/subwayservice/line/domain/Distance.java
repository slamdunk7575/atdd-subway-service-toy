package me.toy.atdd.subwayservice.line.domain;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.Objects;

@Embeddable
public class Distance {

    @Transient
    private static final int MIN_DISTANCE = 0;

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateInitDistance(distance);
        this.distance = distance;
    }

    private void validateInitDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
    }

    public Distance addDistance(Distance newDistance) {
        return new Distance(this.distance += newDistance.distance);
    }

    public Distance minusDistance(Distance newDistance) {
        validateDistance(newDistance.distance);
        return new Distance(this.distance -= newDistance.distance);
    }

    private void validateDistance(int newDistance) {
        if (this.distance <= newDistance) {
            throw new IllegalArgumentException("기존 역사이 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
