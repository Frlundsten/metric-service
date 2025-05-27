package com.fl.adapter.out.mail;

import com.fl.application.domain.model.MetricReport;
import com.fl.application.port.out.create.ForAlertingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class MailSender implements ForAlertingUser {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Override
  public void sendAlert(String message, List<MetricReport> reports) {
    LOG.debug("Sending alert to user: {}", message);
  }
}
