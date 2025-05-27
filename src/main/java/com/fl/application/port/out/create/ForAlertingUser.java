package com.fl.application.port.out.create;

import com.fl.application.domain.model.MetricReport;

import java.util.List;

public interface ForAlertingUser {
    void sendAlert(String message, List<MetricReport> reports);
}
