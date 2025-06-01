package com.fl.adapter.out.mail;

import com.fl.application.domain.model.MetricReport;
import com.fl.application.port.out.notification.ForAlertingUser;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class MailSender implements ForAlertingUser {
    Mailer mailer;
    final String recipient;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MailSender(String mailHost, Integer mailPort, String mailUser, String mailPassword, String recipient) {
        this.mailer = MailerBuilder.withSMTPServer(mailHost, mailPort, mailUser, mailPassword).withTransportStrategy(TransportStrategy.SMTP).buildMailer();
        this.recipient = recipient;
    }

    @Override
    public void sendAlert(String message, List<MetricReport> reports) {
        if (reports == null || reports.isEmpty()) {
            LOG.warn("No reports to send in alert");
            return;
        }
        var currentReport = reports.getLast().data();
        Email email = EmailBuilder.startingBlank()
                .from("Metric-app", "metricalerts@warning.com")
                .to("B.Tables", recipient)
                .withSubject("Metric check fail")
                .withPlainText(message + "\n\n" + currentReport)
                .buildEmail();
        try {
            mailer.sendMail(email);
            LOG.debug("Sending alert to user: {}", message);
        } catch (Exception e) {
            LOG.error("Failed to send alert email", e);
        }
    }
}
