package com.cloudq.cloudq.service;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AWSCloudProviderClient implements CloudProviderClient {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String region;
    private final String service = "ec2";

    public AWSCloudProviderClient(String accessKeyId, String secretAccessKey, String region) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.region = region;
    }

    @Override
    public void launchInstance(Map<String, String> workloadParameters) {
        String instanceType = workloadParameters.get("instanceType");
        String amiId = workloadParameters.get("amiId");

        String requestBody = String.format(
                "{\"ImageId\":\"%s\", \"InstanceType\":\"%s\", \"MinCount\":1, \"MaxCount\":1}",
                amiId, instanceType);

        sendAwsRequest("POST", "https://ec2." + region + ".amazonaws.com/", requestBody);
    }

    @Override
    public Map<String, Object> getMetrics() {
        // Implement metric fetching logic here, making appropriate API calls
        return new HashMap<>();
    }

    @Override
    public void stopInstance(String instanceId) {
        String requestBody = String.format("{\"InstanceIds\":[\"%s\"]}", instanceId);
        sendAwsRequest("POST", "https://ec2." + region + ".amazonaws.com/", requestBody);
    }

    @Override
    public void terminateInstance(String instanceId) {
        String requestBody = String.format("{\"InstanceIds\":[\"%s\"]}", instanceId);
        sendAwsRequest("POST", "https://ec2." + region + ".amazonaws.com/", requestBody);
    }

    @Override
    public Map<String, Object> getInstanceDetails(String instanceId) {
        // Implement instance detail retrieval logic here
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> listRunningInstances() {
        // Implement logic to list running instances using EC2 API
        return List.of();
    }

    private void sendAwsRequest(String method, String endpoint, String body) {
        try {
            HttpURLConnection connection = createConnection(method, endpoint, body);
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Handle response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body: " + response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpURLConnection createConnection(String method, String endpoint, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("X-Amz-Date", getAmzDate());
        connection.setRequestProperty("Authorization", generateAwsSignature(method, endpoint, body));

        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    private String generateAwsSignature(String method, String endpoint, String body) {
        try {
            String canonicalRequest = createCanonicalRequest(method, endpoint, body);
            String stringToSign = createStringToSign(canonicalRequest);
            byte[] signingKey = getSignatureKey(secretAccessKey, getDate(), region, service);
            String signature = bytesToHex(hmacSHA256(signingKey, stringToSign));
            return "AWS4-HMAC-SHA256 Credential=" + accessKeyId + "/" + getCredentialScope() + ", " +
                    "SignedHeaders=content-type;host;x-amz-date, Signature=" + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AWS Signature", e);
        }
    }

    private String createCanonicalRequest(String method, String endpoint, String body) {
        String canonicalUri = "/"; // Root URI for the EC2 API
        String canonicalQueryString = ""; // No query string for this example
        String hashedPayload = bytesToHex(sha256(body));
        String host = endpoint.split("/")[2]; // Extracting host from endpoint
        return String.join("\n",
                method,
                canonicalUri,
                canonicalQueryString,
                String.format("content-type:application/json; charset=UTF-8"),
                String.format("host:%s", host),
                String.format("x-amz-date:%s", getAmzDate()),
                hashedPayload
        );
    }

    private String createStringToSign(String canonicalRequest) {
        return String.join("\n",
                "AWS4-HMAC-SHA256",
                getAmzDate(),
                getCredentialScope(),
                bytesToHex(sha256(canonicalRequest))
        );
    }

    private String getCredentialScope() {
        return String.join("/", getDate(), region, service, "aws4_request");
    }

    private String getAmzDate() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").format(Instant.now());
    }

    private String getDate() {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(Instant.now());
    }

    private byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(kSecret, dateStamp);
        byte[] kRegion = hmacSHA256(kDate, regionName);
        byte[] kService = hmacSHA256(kRegion, serviceName);
        return hmacSHA256(kService, "aws4_request");
    }

    private byte[] hmacSHA256(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
            mac.init(secretKeySpec);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }

    private byte[] sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}