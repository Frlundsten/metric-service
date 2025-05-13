package com.helidon.adapter.out.mail;

import com.helidon.application.domain.model.MetricReport;
import com.helidon.application.port.out.create.ForAlertingUser;
import java.util.List;

public class MailSender implements ForAlertingUser {
  @Override
  public void sendAlert(String message, List<MetricReport> reports) {}
}
