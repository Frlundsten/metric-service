package com.fl.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MetricsDataGenerator {

  // Old metric IDs
  private static final String[] metricIds = {
    "763c080f-46ce-43a5-a0c4-cfe40ab8690f",
    "a5090552-3aea-4bc2-91de-33275d23e9db",
    "5b38d5dd-a5f8-43da-bbc7-783d653710ba",
    "938661ec-4331-4f6a-82ba-7db022dde7b7",
    "a6c247dc-5d66-446a-be60-1daf4d5e8578",
    "18255812-0396-4538-8cbe-71fdfd8faec1",
    "cfe21e71-d415-437f-852a-5a24dc0a76b5",
    "cc359f09-98c1-49b5-9c66-8b8980827a6c",
    "6cc161d9-265c-48de-9ae8-90f410403e55",
    "a8cf3a26-4ac2-4f77-8ee5-4c49647860fe",
    "cce1c24b-85b9-4dc8-8297-0cee780747af",
    "56dc9aa2-f3d8-4005-8695-437b70ee06ca",
    "48174d57-5313-4cbd-a836-c92ffe2ae71e",
    "9ad8d22e-56aa-4791-af92-bec2c785d6f9",
    "ea11c61d-2363-4699-88e2-b4b794b401ee",
    "62a94f10-3501-4cf2-baab-0ee1a6049de3",
    "3093fa23-8380-4080-82a5-05fdf56c3b4b",
    "a76925b8-d219-457b-a31d-5ee7b2ba1af3",
    "d61017c1-81d1-48f5-9578-846f04ddfd56",
    "9cc13a1f-577b-4cd4-b2bd-3f8e61be40fd",
    "f8041f06-d073-4b3b-bcca-e1d3759eaf0a",
    "c4f5b34a-cc94-43b9-8a08-5814d8656798",
    "e9635a1c-c6ed-44d2-8cf9-bb433cc66077",
    "a714c567-af07-4a43-9220-539f833c2ddf",
    "42d52a4a-6009-45fc-a99e-33b14b9a21a0",
    "30258085-9eda-4614-a2bb-352dba03ed0d",
    "8e2085e9-d711-41f5-8cba-fe2de9d1a365",
    "1499471f-c9f6-4e6b-9979-ced94d39bf7f",
    "78307cbe-2132-4d8d-a387-e8f11923bf06",
    "ad52c1ad-7349-41c7-81b4-de281628fc83",
    "62fe88e7-3b27-4ea2-a8ce-cd4e305caad8",
    "864ec0c3-0d9d-4b63-beab-a3df598b2cd5",
    "6fe52172-3083-40c4-b26e-b4afa8a2d61e",
    "2e9a04e1-2eb1-4d40-99a8-bb4f535dabd8",
    "49bd0204-17b4-48fe-a09d-5639f2cc6006",
    "82161f2c-08be-4a7f-8e02-40b1ebf43bb8",
    "a2fa629e-1207-45af-adc9-e50632e75c65",
    "44c777d0-6962-4bdc-a2de-20f341cc7cd8",
    "bf7764c3-493c-46c3-af28-8bf61d35120c",
    "7779bb10-9ef6-45d6-9b40-0f0f055d4256",
    "9747236b-aee3-4511-bc1b-5cc07e62f569",
    "9e06011e-e1bf-478b-8046-5d36da5cf40e",
    "2c6c79a0-120c-40e1-a1db-adb53583ca76",
    "556cfd0b-0b32-4e82-8b81-4c523df26f9c",
    "ac3365c4-164e-4597-81ce-ac4e35e59e60",
    "365b01fa-9c25-4653-93d6-4d3a5e98126c",
    "b893e135-7974-4a30-951b-d4a2d825f2c4",
    "a04e7607-56e9-403e-9a8e-fe908d86f5c4",
    "387474cb-3af7-4769-a3a7-8e3d5dc63b96",
    "1a76a5cc-f1d6-4c0e-929d-7e255c70bf22",
    "2136b32c-a3a7-4d8f-b1c6-41bf3411b84e",
    "00ad5ae6-eb17-4f1a-abf5-29fa488522a8",
    "f60e1869-9ec8-4788-906b-83566feb044a",
    "fe38c67a-0129-4f98-8943-7c4f615ace90",
    "d20535cf-275b-4ee0-a377-e33520373c83",
    "8e362e28-f9d4-4ecd-a904-d0737154935a",
    "773e69b4-05c2-42aa-bcaa-6ed926fda91b",
    "562e2506-6b64-4834-8cf1-c541936fcf31",
    "96b44622-8d65-465b-a0f4-232285b96ad0",
    "bdc5f6fc-fca0-414a-9a25-be4efd816b72",
    "c268d2d2-7949-4fef-8aa8-3ee102054f82",
    "139047ff-1f1f-4400-8b22-77a0a5a52d11",
    "3424e5ca-01c2-46ec-9349-029df2ecfb36",
    "5c77d63b-58f7-49f9-b114-a8197c3c9def",
    "348d68d9-2e51-476a-9b36-c6ec10f13dda",
    "d3cf6dbf-782d-4968-8149-96fea8ecdbd9",
    "a0e0abde-8f05-4f86-8992-41b1e596a3f8",
    "f27c7b00-7c59-4765-afed-91b84206efe9",
    "90b62e9e-b0b1-481b-b815-883d1cc0c33b",
    "06311330-d957-48de-8286-b64d87a9ffd1",
    "bb864553-0c64-4d74-aed8-5d95c3bdf1ea",
    "9a0f652c-b0b7-43bf-a3bb-ade91228936d",
    "fbbc3391-3642-4567-858b-cfb43b0fdc23",
    "23f56297-e3c4-4781-84be-61e056f0d720",
    "b309ed69-3990-48fc-97d3-dacb9e77183e",
    "b89b5130-efa3-4a8b-a880-d9d5d1e82a08",
    "eb805269-0362-4d99-81ef-dba5f257cdbb",
    "021338f9-0a9a-4355-975e-357828730bd2",
    "f2071cb1-f54a-4b8c-8e87-a0aa26d72200",
    "9740c4e5-d7ba-41d5-a5a2-44ea2f6b470a",
    "9a458617-0fa5-48bc-8391-76f7d56039a4",
    "f6190847-b5cc-4e19-a515-cdecb649ba25",
    "379761f1-1075-4924-beab-bb629c207492",
    "b34b51ef-226c-49c9-9ace-be1c31ea513d",
    "5d20b45d-9494-49f7-8a5b-b30b8cc37466",
    "f1ba6e77-a844-4ff4-ae17-8fbae19fae26",
    "82742664-e06d-4091-bbc2-f4deab16cd20",
    "f23315a5-4e5c-4789-9549-db93843e007d",
    "36ba19a4-4aa6-49de-97ab-d4f5e67dbe39",
    "175d83dc-b0a2-4548-8b51-7d51d136c696",
    "36a180bc-732f-4ada-9f33-08ff01f55440",
    "f676533c-301d-48e1-a4b2-01c210e6d40e",
    "99e5cff9-8fbe-40e4-b33d-6ff8ebe83ecd",
    "fd5e12a2-0ac5-411b-bf99-ebf80c1697c0",
    "c3892f8d-9c6e-458d-a073-b8b375c20acc",
    "cea2f4cb-7d7e-4aad-b53c-eb3bee2ee08e",
    "823eb1b8-f2f5-4358-a150-e44a99b8c764",
    "9bd08f20-4c34-4fac-8c3d-27e2aa66c50d",
    "bdb24d65-86cb-4a50-8de9-734a464aff99",
    "53e8d574-d3ab-455c-8878-d14719598046"
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
