package com.fl.application.port.out.notification;

import com.fl.application.domain.model.MetricReport;

import java.util.List;

public interface ForAlertingUser {
    void sendAlert(String message, List<MetricReport> reports);
}
