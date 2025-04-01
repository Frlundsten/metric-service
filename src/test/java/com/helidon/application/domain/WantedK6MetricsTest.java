package com.helidon.application.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WantedK6MetricsTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("DATA_RECEIVED",true),
                Arguments.of("HTTP_REQ_BLOCKED",true),
                Arguments.of("unknown_metric",false)
        );
    }


    @ParameterizedTest
    @MethodSource("data")
    void shouldBeValid(String input, boolean expected) {
        boolean result = WantedK6Metrics.isValid().test(input);

        assertThat(result).isEqualTo(expected);
    }
}
