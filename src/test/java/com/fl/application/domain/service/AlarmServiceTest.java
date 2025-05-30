package com.fl.application.domain.service;

import com.fl.application.port.out.create.ForAlertingUser;
import com.fl.exception.TrendViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;


class AlarmServiceTest {

    private AlarmService alarmService;
    private ForAlertingUser alertUser;

    @BeforeEach
    void setUp() {
        alertUser = mock(ForAlertingUser.class);
        alarmService = new AlarmService(alertUser);
    }

    @Test
    void shouldNotThrowWhenThresholdBetweenFirstAndLastIsNotCrossed() {
        List<Double> p95List = List.of(
                100.0, 105.0
        );

        assertThatNoException().isThrownBy(() -> alarmService.checkP95Trend(p95List));
    }

    @Test
    void shouldThrowWhenIncreasingOverTime() {
        List<Double> p95List = List.of(
                100.0, 101.0, 102.0, 103.0, 104.0
        );

        assertThatException().isThrownBy(() -> alarmService.checkP95Trend(p95List))
                .isInstanceOf(TrendViolationException.class)
                .withMessage("""
                        P95 has consistently increased over the selected period!
                        Settings used:
                        Iteration increase threshold: 0%
                        First to last threshold: 10%
                        Recent runs span: 5
                        """);
    }

    @Test
    void testShouldNotSendAlarm() {
        List<Double> p95List = List.of(
                100.0, 120.0, 112.0, 143.0, 114.0
        );

        assertThatNoException().isThrownBy(() -> alarmService.checkP95Trend(p95List));
    }
}