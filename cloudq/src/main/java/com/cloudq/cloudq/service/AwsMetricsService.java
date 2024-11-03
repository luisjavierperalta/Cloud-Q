package com.cloudq.cloudq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AwsMetricsService {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String region;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper for JSON parsing

    public AwsMetricsService(String accessKeyId, String secretAccessKey, String region) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.region = region;
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> fetchMetrics(String resourceId, String metricName, String namespace) {
        return fetchMetrics(resourceId, metricName, namespace, System.currentTimeMillis() - 3600 * 1000, System.currentTimeMillis());
    }

    public Map<String, Object> fetchMetrics(String resourceId, String metricName, String namespace, long startTime, long endTime) {
        String endpoint = "https://monitoring." + region + ".amazonaws.com";
        String service = "monitoring";
        String requestType = "POST";
        String amzDate = getAmzDate();
        String dateStamp = amzDate.substring(0, 8); // YYYYMMDD

        // Create the request payload
        String requestPayload = createRequestPayload(resourceId, metricName, namespace, startTime, endTime);

        try {
            // Create the canonical request
            String canonicalRequest = createCanonicalRequest(requestType, endpoint, requestPayload, amzDate, dateStamp);
            // Create the string to sign
            String stringToSign = createStringToSign(amzDate, dateStamp, service, canonicalRequest);
            // Create the signature
            String signature = createSignature(dateStamp, stringToSign);

            // Create the final request
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestType);
            connection.setRequestProperty("X-Amz-Date", amzDate);
            connection.setRequestProperty("Authorization", "AWS4-HMAC-SHA256 Credential=" + accessKeyId + "/" + dateStamp + "/" + region + "/" + service + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoOutput(true);

            // Write the request payload
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestPayload.getBytes(StandardCharsets.UTF_8));
            }

            // Check for successful response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                // Process the response to extract metrics
                return parseMetrics(response.toString());
            } else {
                throw new IOException("Failed to fetch metrics. HTTP response code: " + responseCode);
            }

        } catch (IOException e) {
            // Handle network errors and response parsing errors
            e.printStackTrace();
            return Map.of("error", "IO error occurred: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            return Map.of("error", "An error occurred: " + e.getMessage());
        }
    }

    private String getAmzDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    private String createRequestPayload(String resourceId, String metricName, String namespace, long startTime, long endTime) {
        // Build the request payload
        return "Action=GetMetricData&Version=2010-08-01" +
                "&MetricDataQueries.1.Id=m1" +
                "&MetricDataQueries.1.MetricStat.MetricName=" + metricName +
                "&MetricDataQueries.1.MetricStat.Namespace=" + namespace +
                "&MetricDataQueries.1.MetricStat.Period=300" +
                "&MetricDataQueries.1.MetricStat.Stat=Average" +
                "&MetricDataQueries.1.MetricStat.Dimensions.1.Name=InstanceId" +
                "&MetricDataQueries.1.MetricStat.Dimensions.1.Value=" + resourceId +
                "&StartTime=" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date(startTime)) +
                "&EndTime=" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date(endTime)) +
                "&SignatureVersion=4";
    }

    private String createCanonicalRequest(String requestType, String endpoint, String payload, String amzDate, String dateStamp) {
        return requestType + "\n" +
                "/" + "\n" +
                "content-type:application/x-www-form-urlencoded; charset=UTF-8\n" +
                "host:monitoring." + region + ".amazonaws.com\n" +
                "x-amz-date:" + amzDate + "\n\n" +
                "content-type;host;x-amz-date\n" +
                hash(payload);
    }

    private String createStringToSign(String amzDate, String dateStamp, String service, String canonicalRequest) {
        return "AWS4-HMAC-SHA256\n" +
                amzDate + "\n" +
                dateStamp + "/" + region + "/" + service + "/aws4_request\n" +
                hash(canonicalRequest);
    }

    private String createSignature(String dateStamp, String stringToSign) throws Exception {
        byte[] kSecret = ("AWS4" + secretAccessKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSha256(kSecret, dateStamp);
        byte[] kRegion = hmacSha256(kDate, region);
        byte[] kService = hmacSha256(kRegion, "monitoring");
        byte[] kSigning = hmacSha256(kService, "aws4_request");
        return bytesToHex(hmacSha256(kSigning, stringToSign));
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String hash(String data) {
        return bytesToHex(javax.xml.bind.DatatypeConverter.parseHexBinary(data));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Map<String, Object> parseMetrics(String response) {
        try {
            // Parse the JSON response using Jackson
            JsonNode jsonNode = objectMapper.readTree(response);
            Map<String, Object> metrics = new HashMap<>();

            // Navigate through the JSON structure to extract required metrics
            if (jsonNode.has("GetMetricDataResult")) {
                JsonNode metricDataResults = jsonNode.get("GetMetricDataResult").get("MetricDataResults");
                for (JsonNode result : metricDataResults) {
                    String label = result.get("Label").asText();
                    JsonNode values = result.get("Values");
                    metrics.put(label, values);
                }
            } else {
                metrics.put("error", "Invalid response format.");
            }

            return metrics;
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("error", "Failed to parse response: " + e.getMessage());
        }
    }
}