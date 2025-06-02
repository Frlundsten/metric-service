package com.fl.adapter.out.ai;

import com.fl.adapter.in.rest.dto.request.AiMetricReportRequest;
import com.fl.application.port.out.ai.ForContactingAI;
import dev.langchain4j.data.document.DefaultDocument;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
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
import java.util.stream.Collectors;

public class AiContact implements ForContactingAI {
    public static final Logger LOG = LoggerFactory.getLogger(AiContact.class);

    String BASE_URL;
    String aiModel;

    public AiContact(String baseUrl, String aiModel) {
        this.aiModel = aiModel;
        this.BASE_URL = baseUrl;
    }

    @Override
    public void analyzeWithAi() {
        LOG.debug("Ai analyzes without input");
        List<Document> document = FileSystemDocumentLoader.loadDocuments("ai/rag");

        LOG.debug("Setting up RAG data..");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(document, embeddingStore);

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("not needed")
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName("ai/llama3.2:1B-Q4_0")
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        LOG.debug("Sending prompt..");
        String answer = assistant.chat("My p95 is increasing, what metric uses that value?");
        System.out.println(answer);

    }

    @Override
    public void analyzeWithAi(List<AiMetricReportRequest> reports) {
        LOG.debug("Analyzing reports over time..");

        LOG.debug("Setting up RAG data..");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        List<Document> docs = reports.stream()
                .map(report -> {
                    Map<String, Object> metadataMap = new HashMap<>();
                    metadataMap.put("timestamp", report.timestamp().toString());

                    String summary = summarize(report);
                    Metadata metadata = new Metadata(metadataMap);

                    return new DefaultDocument(summary, metadata);
                })
                .collect(Collectors.toList());

        System.out.println(docs);
        EmbeddingStoreIngestor.ingest(docs, embeddingStore);

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("not needed")
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName("ai/llama3.2:1B-Q4_0")
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        LOG.debug("Sending prompt..");
        String answer = assistant.chat("Is the p95 latency trending upward? Should I be concerned?");
        System.out.println(answer);
    }


    public String summarize(AiMetricReportRequest report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Run ID: ").append(report.id()).append("\n");
        sb.append("Timestamp: ").append(report.timestamp()).append("\n");

        for (AiMetricReportRequest.AiMetricRequest metric : report.metrics()) {
            sb.append("Metric: ").append(metric.name()).append("\n");
            var values = metric.values();
            sb.append("  - ").append("med").append(": ").append(values.med()).append("s\n");
            sb.append("  - ").append("min").append(": ").append(values.min()).append("s\n");
            sb.append("  - ").append("max").append(": ").append(values.max()).append("s\n");
            sb.append("  - ").append("avg").append(": ").append(values.avg()).append("s\n");
            sb.append("  - ").append("p(95)").append(": ").append(values.p95()).append("s\n");
            sb.append("  - ").append("p(90)").append(": ").append(values.p90()).append("s\n");
        }

        return sb.toString();
    }


//    interface Assistant {
//        @SystemMessage("""
//                Use only the information in the context below to answer the question.
//                If the answer is not in the context, respond with: "I don't know."
//
//                Question:
//                {{question}}
//
//                Answer:
//                """)
//        @UserMessage("{{question}}")
//        String chat(@V("question") String question);
//    }

    interface Assistant {
        @SystemMessage("""
                The following context documents are sorted in ascending order by timestamp.
                You can assume that the first document is the latest run.
                
                Compare the p(95) latency of the latest run to previous runs, and say whether it has increased, decreased, or remained stable.
                
                Only use the information from the documents. If not enough information is available, respond with "I don't know".
                
                Question:
                {{question}}
                
                Answer:
                """)
        @UserMessage("{{question}}")
        String chat(@V("question") String question);
    }


}
