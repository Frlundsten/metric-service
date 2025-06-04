package com.fl.adapter.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fl.adapter.common.Mapper;
import com.fl.adapter.common.ObjectMapperFactory;
import com.fl.application.domain.model.MetricReport;
import com.fl.application.port.out.analyze.ForDataAnalysis;
import com.fl.exception.AnalyzeAdapterException;
import dev.langchain4j.data.document.DefaultDocument;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AiContact implements ForDataAnalysis {
    public static final Logger LOG = LoggerFactory.getLogger(AiContact.class);
    public final ChatModel chatModel;
    ObjectMapper mapper;

    public AiContact(String baseUrl, String aiModel) {
        mapper = ObjectMapperFactory.create();
        chatModel = OpenAiChatModel.builder()
                .apiKey("not needed")
                .baseUrl(baseUrl)
                .modelName(aiModel)
                .temperature(0.0)
                .build();
    }

    @Override
    public String analyzeData(List<MetricReport> reports) {
        LOG.debug("Setting up RAG data..");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        var reportResources = reports.stream().map(AiMetricReportResource::toAiResource).toList();
        var dynamicMetricDocs = getDocFromMetrics(reportResources);

        LOG.debug("RAG data saved: {}", dynamicMetricDocs);

        EmbeddingStoreIngestor.ingest(dynamicMetricDocs, embeddingStore);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        LOG.debug("Sending prompt..");
        String prompt = """
                     You are a data analyst reviewing performance metric reports for a web service.
                         Each report contains metrics like 'http_req_duration' with statistics: max, min, avg, median (med), p90, and p95.
                         Reports have metadata including 'index' (0 is most recent) and 'RECENT' for the latest run.
                
                         Compare the most recent run (index 0 or RECENT) to previous runs (index 1, 2, ...) and highlight:
                         - Significant regressions or improvements, especially in p95 and avg.
                         - Any concerning trends.
                         - Summarize overall stability.
                
                         Here are the reports (in JSON format):
                """;
        try {
            String answer = assistant.chat(prompt);
            LOG.debug(answer);
            return answer;
        } catch (Exception e) {
            LOG.error("Failed to get chat response", e);
            throw new AnalyzeAdapterException(
                    "Unable to reach the language model. Please check if it's running and ensure the environment variables are set correctly.");
        }
    }

    private List<Document> getDocFromMetrics(List<AiMetricReportResource> reportResources) {
        reportResources = sortList(reportResources);
        AtomicInteger index = new AtomicInteger(0);

        return reportResources.stream()
                .map(report -> {
                    int i = index.getAndIncrement();
                    Map<String, Object> metadataMap = new HashMap<>();
                    metadataMap.put("timestamp", report.timestamp().toString());
                    metadataMap.put("id", report.id());
                    metadataMap.put("index", i);
                    if (i == 0) {
                        metadataMap.put("RECENT", "RECENT");
                    }

                    Metadata metadata = new Metadata(metadataMap);
                    return new DefaultDocument(Mapper.toJson(toResource(report)), metadata);
                })
                .collect(Collectors.toList());
    }

    private List<AiMetricReportResource> sortList(List<AiMetricReportResource> reportResources) {
        return reportResources.stream()
                .sorted((r1, r2) -> r2.timestamp().compareTo(r1.timestamp()))
                .toList();
    }

    @Override
    public String analyzeRecent(List<MetricReport> requests) {
        return "";
    }

    interface Assistant {
        @SystemMessage("""
                You are a performance analysis assistant. The user provides structured metric reports (JSON format) from performance test runs.
                
                    Each report includes a timestamp, an id, a unique index indicating the order (0 = most recent), and a list of metrics. The most recent report has metadata RECENT.
                
                    Metrics include latency percentiles like p95, p90, average, max, min, and median.
                
                    Important: For latency metrics (such as 'http_req_duration'), **lower values mean better performance**. So, if p95 or avg increases, it indicates a performance regression; if it decreases, it indicates improvement.
                
                    Your job is to:
                    - Compare the most recent run (index 0 or RECENT) against previous ones (index 1, 2, ...).
                    - Focus especially on p95, but also note significant changes in avg, max, or http_req_failed if available.
                    - Clearly state whether performance improved (values decreased), regressed (values increased), or stayed stable.
                    - If p95 or avg increases by a large margin, issue a **warning**.
                    - If results are consistent, just say things look stable.
                    - Do not guess or assume; only use what is in the input.
                    - Use simple, clear language suitable for engineers monitoring performance trends.
                
                    Analyze all provided reports carefully before making conclusions.
                    You must only use the information in the provided documents. Do not use outside knowledge.
                """)
        @UserMessage("{{question}}")
        String chat(@V("question") String question);
    }

    public JsonNode toResource(AiMetricReportResource report) {
        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("id", report.id());
        reportNode.put("timestamp", report.timestamp().toString());

        ArrayNode metricsArray = mapper.createArrayNode();
        for (AiMetricResource metric : report.metricList()) {
            metricsArray.add(toResource(metric));
        }

        reportNode.set("metricList", metricsArray);
        return reportNode;
    }

    public JsonNode toResource(AiMetricResource metric) {
        ObjectNode metricNode = mapper.createObjectNode();
        metricNode.put("name", metric.name());
        metricNode.put("type", metric.type());

        ObjectNode valuesNode = mapper.createObjectNode();
        valuesNode.put("max", metric.max());
        valuesNode.put("min", metric.min());
        valuesNode.put("avg", metric.avg());
        valuesNode.put("med", metric.med());
        valuesNode.put("p95", metric.p95());
        valuesNode.put("p90", metric.p90());

        metricNode.set("values", valuesNode);
        return metricNode;
    }


}
