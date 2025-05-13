package com.helidon.application.port.out.create;

import com.helidon.application.domain.model.MetricReport;

import java.util.List;

public interface ForAlertingUser {
    void sendAlert(String message, List<MetricReport> reports);
}
