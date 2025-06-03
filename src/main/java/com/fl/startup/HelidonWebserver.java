package com.fl.startup;

import com.fl.adapter.common.Mapper;
import com.fl.adapter.common.MetricReportTypeMapperProvider;
import com.fl.adapter.common.MetricTypeMapperProvider;
import com.fl.adapter.in.rest.AnalyzeHandler;
import com.fl.adapter.in.rest.CreateMetricsHandler;
import com.fl.adapter.in.rest.DelegatingService;
import com.fl.adapter.in.rest.RecentReportsHandler;
import com.fl.adapter.in.rest.ReportTimespanHandler;
import com.fl.adapter.out.ai.AiContact;
import com.fl.adapter.out.mail.MailSender;
import com.fl.adapter.out.persistence.MetricJDBCRepository;
import com.fl.application.domain.service.AlarmService;
import com.fl.application.domain.service.AnalyzeService;
import com.fl.application.domain.service.MetricService;
import com.fl.application.port.in.analyze.ForAnalyzingData;
import com.fl.application.port.in.create.ForCreateMetrics;
import com.fl.application.port.in.manage.ForManagingMetrics;
import com.fl.application.port.out.analyze.ForDataAnalysis;
import com.fl.application.port.out.create.ForPersistingMetrics;
import com.fl.application.port.out.manage.ForManagingStoredMetrics;
import com.fl.application.port.out.notification.ForAlertingUser;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelidonWebserver {

    public static final Logger LOG = LoggerFactory.getLogger(HelidonWebserver.class);

    public void start() {
        Config config = Config.create();
        Mapper mapper = new Mapper();
        DbClient client =
                DbClient.builder(config.get("db"))
                        .mapperProvider(new MetricReportTypeMapperProvider())
                        .mapperProvider(new MetricTypeMapperProvider())
                        .build();

        DelegatingService delegatingService = getDelegatingService(client, mapper, config);

        WebServer.builder()
                .config(config.get("server"))
                .routing(getRouting(delegatingService))
                .build()
                .start();

        LOG.debug("HelidonWebserver started");
    }

    private HttpRouting.Builder getRouting(DelegatingService delegatingService) {
        return HttpRouting.builder().register("/metrics", delegatingService);
    }

    private DelegatingService getDelegatingService(DbClient client, Mapper mapper, Config config) {

        // Outbound adapters implementing outbound ports (use cases)
        MetricJDBCRepository repository = new MetricJDBCRepository(client);
        ForPersistingMetrics persister = repository;
        ForManagingStoredMetrics manager = repository;
        ForDataAnalysis aiContact = setupAiContact(config);
        ForAlertingUser alertUser = setupMailSender(config);

        /*
         * Domain services
         * Encapsulate core business logic and coordinate interactions between ports.
         * May call outbound ports.
         */
        AnalyzeService analyzeService = new AnalyzeService(aiContact, manager);
        AlarmService alarmService = new AlarmService(alertUser);
        MetricService metricService = new MetricService(persister, manager, alarmService);

        // MetricService implements both inbound ports.
        ForCreateMetrics createMetrics = metricService;
        ForManagingMetrics manageMetrics = metricService;

        // AnalyzeService uses outbound port
        ForAnalyzingData analyzingData = analyzeService;

        /*
        Inbound adapters (application entry points / driving adapters)
        Responsible for handling incoming requests and delegating them to the appropriate use case (port).
         */
        AnalyzeHandler analyzeHandler = new AnalyzeHandler(analyzingData);
        CreateMetricsHandler createMetricsHandler = new CreateMetricsHandler(createMetrics, mapper);
        ReportTimespanHandler reportTimespanHandler = new ReportTimespanHandler(manageMetrics);
        RecentReportsHandler recentReportshandler = new RecentReportsHandler(manageMetrics);

        return new DelegatingService(createMetricsHandler, reportTimespanHandler, recentReportshandler, analyzeHandler);
    }

    private ForDataAnalysis setupAiContact(Config config) {
        var aiUrl = config.get("AI_RUNNER_URL").asString().get();
        var aiModel = config.get("AI_RUNNER_MODEL").asString().get();
        LOG.debug("Setting up AI analyzer with model ({}) and base url ({})", aiModel, aiUrl);
        return new AiContact(aiUrl, aiModel);
    }

    private ForAlertingUser setupMailSender(Config config) {
        var mailHost = config.get("mail.host").asString().get(String.class);
        var mailPort = config.get("mail.port").asInt().get(Integer.class);
        var mailUser = config.get("mail.user").asString().get(String.class);
        var mailPassword = config.get("mail.password").asString().get(String.class);
        var recipient = config.get("mail.recipient").asString().get(String.class);
        LOG.debug("Setting up mail at host ({}) with recipient ({})", mailHost, recipient);
        return new MailSender(mailHost, mailPort, mailUser, mailPassword, recipient);
    }

}
