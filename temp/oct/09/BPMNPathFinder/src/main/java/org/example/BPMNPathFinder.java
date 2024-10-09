package org.example;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class BPMNPathFinder {

    private static final String BPMN_URL = "https://n35ro2ic4d.execute-api.eu-central-1.amazonaws.com/prod/engine-rest/process-definition/key/invoice/xml";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Please provide exactly two arguments: startNodeId and endNodeId.");
            System.exit(-1);
        }

        String startNodeId = args[0];
        String endNodeId = args[1];

        try {
            String bpmnXml = fetchBPMNXml();
            BpmnModelInstance modelInstance = parseBPMNXml(bpmnXml);
            Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();

            // Build adjacency list
            Map<String, List<String>> graph = buildGraph(process);

            // Check if start and end nodes exist
            if (!graph.containsKey(startNodeId)) {
                System.err.println("Start node ID not found in the BPMN diagram.");
                System.exit(-1);
            }

            if (!graph.containsKey(endNodeId)) {
                System.err.println("End node ID not found in the BPMN diagram.");
                System.exit(-1);
            }

            // Find path using DFS
            List<String> path = findPathDFS(graph, startNodeId, endNodeId, new HashSet<>(), new ArrayList<>());

            if (path == null || path.isEmpty()) {
                System.err.println("No path found between the specified nodes.");
                System.exit(-1);
            }

            // Print the path
            System.out.println("The path from " + startNodeId + " to " + endNodeId + " is:");
            System.out.println(path);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static String fetchBPMNXml() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(BPMN_URL);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new IOException("Failed to fetch BPMN XML. HTTP status code: " + status);
            }

            InputStream content = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(content);
            JsonNode bpmnXmlNode = rootNode.get("bpmn20Xml");
            if (bpmnXmlNode == null) {
                throw new IOException("bpmn20Xml field not found in the response.");
            }
            return bpmnXmlNode.asText();
        }
    }

    private static BpmnModelInstance parseBPMNXml(String bpmnXml) throws IOException {
        try (InputStream is = new java.io.ByteArrayInputStream(bpmnXml.getBytes())) {
            return Bpmn.readModelFromStream(is);
        }
    }

    private static Map<String, List<String>> buildGraph(Process process) {
        Map<String, List<String>> graph = new HashMap<>();

        Collection<FlowNode> flowNodes = process.getChildElementsByType(FlowNode.class);

        for (FlowNode node : flowNodes) {
            String nodeId = node.getId();
            List<String> outgoing = new ArrayList<>();
            for (SequenceFlow flow : node.getOutgoing()) {
                outgoing.add(flow.getTarget().getId());
            }
            graph.put(nodeId, outgoing);
        }

        return graph;
    }

    private static List<String> findPathDFS(Map<String, List<String>> graph, String current, String end,
                                            Set<String> visited, List<String> path) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            return new ArrayList<>(path);
        }

        for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                List<String> result = findPathDFS(graph, neighbor, end, visited, path);
                if (result != null) {
                    return result;
                }
            }
        }

        path.remove(path.size() - 1);
        return null;
    }
}
