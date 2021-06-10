package me.toy.atdd.subwayservice.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DistanceTest {
    private Distance distance5;
    private Distance distance15;
    private Distance distance20;

    @BeforeEach
    void setUp() {
        distance5 = new Distance(5);
        distance15 = new Distance(15);
        distance20 = new Distance(20);
    }

    @DisplayName("길이 더하기")
    @Test
    void addDistanceTest() {
        // when & then
        assertThat(distance5.addDistance(distance15)).isEqualTo(distance20);
    }

    @DisplayName("길이 빼기")
    @Test
    void minusDistanceTest() {
        // when & then
        assertThat(distance20.minusDistance(distance5)).isEqualTo(distance15);
    }

    @DisplayName("음수값으로 길이 생성시 예외 발생")
    @ParameterizedTest
    @ValueSource(ints = {-10, -20})
    void negativeDistance(int invalidValue) {
        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Distance distance = new Distance(invalidValue);
        });
    }
}
