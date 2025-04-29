package com.helidon.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MetricsDataGenerator {

  // Old metric IDs
  private static final String[] metricIds = {
    "24133795-50bd-46b7-bf9d-19e2ba2d270c",
    "1d8afab6-4f5d-49c6-9fae-f1914fb32ec2",
    "d339f9fc-9364-46a4-9988-513498884c64",
    "68236642-9ea6-4ada-a31a-6ff6a610b220",
    "0b7875c3-26a8-498c-b65f-80ddea9cfe5b",
    "ce124c8d-437b-48b4-baa5-5cb25024ad14",
    "2866b211-8e8a-44e4-9f6a-72ebab4f3907",
    "7b04b6df-7396-4f96-9345-24e742ac2c49",
    "7bb467ac-6187-4ef9-8f8f-94e2518a49bc",
    "9eaf3908-9a19-41f7-ab95-56603fc87803",
    "35967d92-4494-49ed-9a1a-4b79a5695466",
    "6ff1c416-344b-45f5-95bf-d64236576b4b",
    "03349fc1-7bec-4739-a9e9-52a53c8a4ad0",
    "7f4be787-1579-48b1-ad26-2fd0532e7eb7",
    "550199a2-dd76-4a68-bb56-6c9a173bf523",
    "1de3bd04-64ed-49cc-9ebf-3f5fbc8eb147",
    "0ff980d9-399b-4418-b92a-1084c9715d9a",
    "4ec08999-f035-41dc-92bb-dab3fe7d445e",
    "941e18b9-a7ac-4d0f-af22-4cb01011ca6c",
    "ead88cf7-0a28-48d4-9a65-66f42830d26b",
    "4ba2b484-7ac6-4a2f-a42c-c99c85ec7c58",
    "7fc0c28d-0e5e-4a71-9cce-902c2204a1d5",
    "e3010c83-fba6-4a23-b6cf-cbc382fe9a9d",
    "22195d5e-fb28-43cf-b8a0-92de54ebdb97",
    "f35d8778-3606-4b0d-9f74-f84a7563dd6b",
    "2c772aea-c4fd-424e-a47e-872c925cede3",
    "1bdce93c-11e0-4d13-86e7-9f4fa608c851",
    "09c2950c-c3bc-4586-bfde-8552cbc9d66e",
    "8379adf8-aa73-446e-87b1-adf2fbdc26f8",
    "8b11ee07-da26-445e-aa3c-9813a30059f9",
    "a8016940-7856-4709-b95d-f39d3d70f3a2",
    "af812897-7a28-4ab2-b2cb-336bed1d1aaa",
    "7d5115c1-9b6c-43fe-b887-5f1b385d5cd5",
    "a57f3598-b3b2-4055-87bb-8ae398b1b20a",
    "c8635286-c2ad-443a-af85-9d0a049ea4e2",
    "f833e202-d4e3-459a-9db4-55c118563679",
    "7433f01a-880d-4c28-bf65-68e15f8ee229",
    "675878cd-0a6c-4dab-9160-219654e1fe82",
    "c0955b30-ee3e-4ab0-a56c-989b66450dab",
    "8a6659f4-e051-4d7a-86cc-4ea1d1c36d6a",
    "9b46019d-4d98-4488-b1d7-8019c981cd30",
    "650981e3-e75d-42cb-b3c6-4c7c251659b8",
    "54058c0a-4beb-40c4-88bd-5989eba966d4",
    "394a3658-be62-423d-949f-5fba4fe1cbd7",
    "a8975d55-681a-4fa0-ade6-633a8622944b",
    "4aed93e4-6b8c-4205-88aa-b895b49f1343",
    "3cd83634-3391-41ab-96ab-38087657b989",
    "ab0864fa-6b06-422c-a888-fb47c4599b40",
    "4f45d13c-2502-4a9b-b4af-8341ad49f721",
    "f7fcff6a-7fb0-4ceb-8e10-72771e952bb1",
    "8cfd9601-dd96-48f5-92dd-18a4aa0d49f2",
    "ef54458c-1cc1-47ad-9d90-afad2129f199",
    "9cf738dd-2543-45c5-8d6f-c0bb3fca4f97",
    "aba084e0-eff6-4484-a232-2a68acbfc93a",
    "8595f8da-5eb0-405d-b5ef-568cf14aa77d",
    "d891aff0-28e0-4aac-a4d9-331cd6c993f6",
    "8144b3f5-fe21-42f3-836d-0aa039802d85",
    "9d0d65a1-52a5-4e5f-94a2-bd6419a49991",
    "23f73706-ddc9-49b4-a20b-144d3d7b7f24",
    "2c97c6d2-a441-44fc-8ca2-4cdbb36ac125",
    "c1f7ebee-37e9-4453-93dc-bc2fe3e9849a",
    "ed5908c4-c583-433c-b191-a4685db393b8",
    "4f51551a-1ef1-46f6-8f0d-4ae7b6bcbeef",
    "c0803f7c-ef73-4ee7-a9f5-9aa63dd11bfb",
    "2686c5a3-6be6-434b-8117-4b60c81470c6",
    "39d4ee63-6c1f-4a16-bbde-2eeb15f6a723",
    "ab9e0daa-ef44-4d14-b7ec-a8a1def7ff32",
    "5b251775-d14e-400f-8fb3-d863beb41229",
    "7c0373ad-b000-494b-8325-d0d4a137790a",
    "1e674ff3-f3ea-4754-8c4e-bd5e79d81276",
    "30660b91-5586-4638-956b-b8a4db8413c7",
    "0e4aa21d-c86a-4c15-8d2c-660665674dc6",
    "b2e5f2eb-e825-4ac6-9831-b74d54caaf51",
    "402234d4-759d-4b36-80e0-5d82ffa86731",
    "99a8c6bb-e924-4a1a-9683-0ed614d58c7e",
    "780aeba3-690c-44b8-98cf-e083730c0ecc",
    "c374d232-30c3-42df-9a74-3b919cbd99b1",
    "5c19cdaf-c0d2-4fe4-9b16-5b653e0ee9d8",
    "a17f2d30-39c3-4a59-bbec-d1629eeef1c6",
    "76145c46-89a7-4165-ba71-78c9ef193d5c",
    "8ebc49ec-e114-453c-8590-a87107826374",
    "2b1b0c31-4f4c-49c6-9119-1cfe35907f4e",
    "27bf3f67-ce0d-4ffa-bc21-e6dc8a66a520",
    "6edbfca4-8f7a-418b-b323-b7c1766801e3",
    "d7d03585-8368-4703-9bb5-457a13751447",
    "0ab17003-aa3e-421a-a18c-485e881ffcb1",
    "421776fa-4cd4-48e7-b505-e4d44e626e4c",
    "df91619e-728f-4ace-a553-8e651375f26d",
    "476282af-8a5d-4cb8-91cd-bd69fb9b5d13",
    "5010f2a8-3be9-44d8-bb4a-f98b50754825",
    "4772a234-15c0-4541-a149-68fd71a85223",
    "a883de49-6dac-4999-a653-ef01a939ceaf",
    "6ad29115-89fa-44d0-a7d6-6491c49ebcc7",
    "629bc40a-92cf-449b-8244-99701b44b7bd",
    "f3df824f-6b04-4260-b879-327783abf713",
    "46514cf5-e104-430a-88e8-fb040c8f3790",
    "c7bf2365-ddc3-4031-b6c6-df04f456d3d8",
    "1633affd-4ca2-4f0b-8336-2f16be574660",
    "b0522214-0472-4f79-9f40-324406e2b556",
    "2cf4fb8c-a2ad-41fa-a268-d3d35d65078b"
  };

  // Metric names (corresponding to the metricIds)
  private static final Map<String, String> metricNames = new HashMap<>();

  static {
    metricNames.put("http_req_failed", "rate");
    metricNames.put("data_received", "counter");
    metricNames.put("http_req_blocked", "trend");
    metricNames.put("http_req_receiving", "trend");
    metricNames.put("iterations", "counter");
    metricNames.put("data_sent", "counter");
    metricNames.put("iteration_duration", "trend");
    metricNames.put("http_req_sending", "trend");
    metricNames.put("http_req_duration", "trend");
    metricNames.put("http_req_tls_handshaking", "trend");
    metricNames.put("vus_max", "gauge");
    metricNames.put("http_req_waiting", "trend");
    metricNames.put("http_req_connecting", "trend");
    metricNames.put("vus", "gauge");
    metricNames.put("http_reqs", "counter");
  }

  public static void generateCsv(String path) throws IOException {
    FileWriter writer = new FileWriter(path);
    writer.write("id,name,metrics_id,type,values\n");

    Random rand = new Random();

    // Loop over the metric IDs and generate data for each
    for (String metricId : metricIds) {
      for (Map.Entry<String, String> name : metricNames.entrySet()) {
        UUID id = UUID.randomUUID();
        String type = name.getValue();
        String valuesJson = getRandomMetricValues(type);

        // Write the data to the CSV file
        writer.write(String.format("%s,\"%s\",%s,%s,%s\n", id, name.getKey(), metricId, type, valuesJson));
      }
    }

    writer.close();
  }

  // Helper method to get a random metric type
  private static String getRandomMetricType() {
    String[] types = {"rate", "counter", "trend", "gauge"};
    Random rand = new Random();
    return types[rand.nextInt(types.length)];
  }

  // Helper method to generate a random "values" JSON string based on the type
  private static String getRandomMetricValues(String type) {
    Random rand = new Random();
    String values = "";

    // Generate random values based on the metric type
    switch (type) {
      case "rate":
        values =
            String.format(
                "\"{\"\"rate\"\":%s,\"\"passes\"\":%s,\"\"fails\"\":%s}\"",
                rand.nextDouble() * 100, rand.nextDouble() * 100, rand.nextDouble() * 100);
        break;
      case "counter":
        values =
            String.format(
                "\"{\"\"count\"\":%s,\"\"rate\"\":%s}\"",
                rand.nextDouble() * 1000, rand.nextDouble() * 100);
        break;
      case "trend":
        values =
            String.format(
                "\"{\"\"max\"\":%s,\"\"min\"\":%s,\"\"avg\"\":%s,\"\"med\"\":%s,\"\"p(95)\"\":%s,\"\"p(90)\"\":%s}\"",
                rand.nextDouble() * 10,
                rand.nextDouble() * 10,
                rand.nextDouble() * 10,
                rand.nextDouble() * 10,
                rand.nextDouble() * 10,
                rand.nextDouble() * 10);
        break;
      case "gauge":
        values =
            String.format(
                "\"{\"\"value\"\":%s,\"\"min\"\":%s,\"\"max\"\":%s}\"",
                rand.nextDouble() * 100, rand.nextDouble() * 100, rand.nextDouble() * 100);
        break;
    }

    return values;
  }

  public static void main(String[] args) {
    try {
      generateCsv("metric_filled.csv");
      System.out.println("metric_filled.csv generated successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
