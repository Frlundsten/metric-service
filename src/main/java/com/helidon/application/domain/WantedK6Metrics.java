package com.helidon.application.domain;

import java.util.function.Predicate;

public enum WantedK6Metrics {
    HTTP_REQ_FAILED,
    DATA_RECEIVED,
    HTTP_REQ_BLOCKED,
    HTTP_REQ_RECEIVING,
    ITERATIONS,
    DATA_SENT,
    ITERATION_DURATION,
    HTTP_REQ_SENDING,
    HTTP_REQ_DURATION,
    HTTP_REQ_TLS_HANDSHAKING,
    VUS_MAX,
    HTTP_REQ_WAITING,
    HTTP_REQ_CONNECTING,
    VUS,
    HTTP_REQS;

    public static Predicate<String> isValid() {
        return value -> {
            try {
                WantedK6Metrics.valueOf(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        };
    }
}
