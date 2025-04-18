package com.helidon.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class DummyDataGenerator {

  public static void generateCsv(String path) throws IOException {
    FileWriter writer = new FileWriter(path);
    writer.write("id,data,created_at,repository_id\n");

    LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 12, 0);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    for (int i = 0; i < 100; i++) {
  String JSON_DATA =
      "{\"http_req_failed\":{\"type\":\"rate\",\"contains\":\"default\",\"values\":{\"rate\":0.0,\"passes\":0.0,\"fails\":100.0}},\"data_received\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":10600.0,\"rate\":10"+((int)(Math.random() * 90) + 10)+".5548080483122}},\"http_req_blocked\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":4.3165,\"min\":0.0,\"avg\":0.4172509999999999,\"med\":0.0,\"p(95)\":4."+((int)(Math.random() * 90) + 10)+"65,\"p(90)\":0."+((int)(Math.random() * 90) + 10)+"050000000003124}},\"http_req_receiving\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.6742,\"min\":0.0,\"avg\":0.0"+((int)(Math.random() * 90) + 10)+"747,\"med\":0.0,\"p(95)\":0.0"+((int)(Math.random() * 90) + 10)+"124999999998646,\"p(90)\":0.0}},\"iterations\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9."+((int)(Math.random() * 90) + 10)+"6366113663323}},\"data_sent\":{\"type\":\"counter\",\"contains\":\"data\",\"values\":{\"count\":8500.0,\"rate\":8"+((int)(Math.random() * 90) + 10)+".8411196613823}},\"iteration_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1005.6491,\"min\":10"+((int)(Math.random() * 90) + 10)+".3286,\"avg\":1001.3614669999998,\"med\":1000.9534,\"p(95)\":10"+((int)(Math.random() * 90) + 10)+".08385,\"p(90)\":10"+((int)(Math.random() * 90) + 10)+".57965}},\"http_req_sending\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.026425999999999998,\"med\":0.0,\"p(95)\":0."+((int)(Math.random() * 90) + 10)+"74,\"p(90)\":0.0}},\"http_req_duration\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1."+((int)(Math.random() * 90) + 10)+"89,\"min\":0.0,\"avg\":0."+((int)(Math.random() * 90) + 10)+"93939999999998,\"med\":0.6349,\"p(95)\":1.0441,\"p(90)\":0."+((int)(Math.random() * 90) + 10)+"42}},\"http_req_tls_handshaking\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.0,\"min\":0.0,\"avg\":0.0,\"med\":0.0,\"p(95)\":0.0,\"p(90)\":0.0}},\"vus_max\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":"+((int)(Math.random() * 9) + 1)+".0}},\"http_req_waiting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":1.0505,\"min\":0.0,\"avg\":0.4952209999999996,\"med\":0.5302,\"p(95)\":0."+((int)(Math.random() * 90) + 10)+"1554999999999,\"p(90)\":0.6742}},\"http_req_connecting\":{\"type\":\"trend\",\"contains\":\"time\",\"values\":{\"max\":0.5284,\"min\":0.0,\"avg\":0.02642,\"med\":0.0,\"p(95)\":0.026419999999998497,\"p(90)\":0.0}},\"vus\":{\"type\":\"gauge\",\"contains\":\"default\",\"values\":{\"value\":10.0,\"min\":10.0,\"max\":10.0}},\"http_reqs\":{\"type\":\"counter\",\"contains\":\"default\",\"values\":{\"count\":100.0,\"rate\":9.9"+((int)(Math.random() * 90) + 10)+"366113663323}}}";
      String id = UUID.randomUUID().toString();
      String escapedJson = JSON_DATA.replace("\"", "\"\"");
      String date = startDate.plusDays(i).format(formatter) + "Z";
        Random rand = new Random();
        String repository = "repo-" + (rand.nextInt(3) + 1);

      writer.write(String.format("%s,\"%s\",%s,%s\n", id, escapedJson, date, repository));
    }

    writer.close();
  }

  public static void main(String[] args) {
    try {
      generateCsv("metrics.csv");
      System.out.println("metrics.csv generated successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
