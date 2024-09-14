/*
 * package com.newbusiness.one4all.util;
 * 
 * import com.fasterxml.jackson.databind.ObjectMapper; import
 * com.fasterxml.jackson.databind.SerializationFeature;
 * 
 * import java.io.File; import java.io.IOException; import java.util.ArrayList;
 * import java.util.List;
 * 
 * public class DummyOrgChartGenerator { private static class Consumer { public
 * String parentConsumerNo; public String stage; public String consumerNo_child;
 * public String consumerName; public String consumer_Mobile; public String
 * consumer_HelpingAmount; public String paymentStatus; public String
 * referralAmount;
 * 
 * public Consumer(String parentConsumerNo, int stage, int id) {
 * this.parentConsumerNo = parentConsumerNo; this.stage =
 * Integer.toString(stage); this.consumerNo_child = String.format("O4AA4O%07d",
 * id); this.consumerName = "Name" + id; this.consumer_Mobile =
 * String.valueOf(9000000000L + id); this.consumer_HelpingAmount = "1000";
 * this.paymentStatus = "UNPAID"; this.referralAmount = "NA"; } }
 * 
 * public static void main(String[] args) { List<Consumer> data = new
 * ArrayList<>(); int currentId = 1; // Start ID from O4AA4O0000001 String
 * rootConsumerNo = String.format("O4AA4O%07d", currentId);
 * 
 * // Add the root node (parentConsumerNo is itself for clarity in root
 * reference) data.add(new Consumer(rootConsumerNo, 0, currentId)); currentId++;
 * 
 * // Generate consumers for each subsequent stage for (int stage = 1; stage <=
 * 20; stage++) { int numNodes = (int) Math.pow(2, stage); for (int i = 0; i <
 * numNodes; i++) { int parentIndex = ((i / 2) + (int) Math.pow(2, stage - 1)) -
 * 1; String parentConsumerNo = String.format("O4AA4O%07d", parentIndex + 1);
 * data.add(new Consumer(parentConsumerNo, stage, currentId)); currentId++; } }
 * 
 * // Convert the data into JSON and save to the Downloads folder ObjectMapper
 * mapper = new ObjectMapper();
 * mapper.enable(SerializationFeature.INDENT_OUTPUT);
 * 
 * // Define the path to the Downloads folder: String userHome =
 * System.getProperty("user.home"); File downloadsPath = new File(userHome,
 * "Downloads/org_data.json");
 * 
 * try { mapper.writeValue(downloadsPath, data); System.out.
 * println("Data generation complete. JSON file created in Downloads folder.");
 * } catch (IOException e) { e.printStackTrace(); } } }
 */