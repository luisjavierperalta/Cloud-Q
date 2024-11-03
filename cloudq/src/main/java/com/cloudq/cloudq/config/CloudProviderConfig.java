package com.cloudq.cloudq.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.ibm.cloud.objectstorage.auth.DefaultAWSCredentialsProviderChain;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidRequestException;
import software.amazon.awssdk.services.secretsmanager.model.DecryptionFailureException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class CloudProviderConfig {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper;

    public CloudProviderConfig(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
        this.objectMapper = new ObjectMapper(); // Initialize Jackson ObjectMapper
    }

    // AWS S3 Client Configuration
    @Bean
    public S3Client awsS3Client() {
        AwsBasicCredentials awsCredentials = getAwsCredentials();
        return S3Client.builder()
                .region(Region.US_EAST_1) // Adjust based on your needs
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    private AwsBasicCredentials getAwsCredentials() {
        String secretName = "aws_access_secret";
        GetSecretValueResponse secretValueResponse = getSecretValue(secretName);
        return parseAwsCredentials(secretValueResponse.secretString());
    }

    // Parse AWS credentials from JSON
    private AwsBasicCredentials parseAwsCredentials(String secretString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(secretString);
            String accessKey = jsonNode.get("accessKey").asText();
            String secretKey = jsonNode.get("secretKey").asText();
            return AwsBasicCredentials.create(accessKey, secretKey);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse AWS credentials from secret", e);
        }
    }

    // GCP Storage Client Configuration
    @Bean
    public Storage gcpStorageClient() {
        String gcpKeyFileJson = getGcpKeyFile();
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(gcpKeyFileJson.getBytes()));
            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create GCP Storage client", e);
        }
    }

    private String getGcpKeyFile() {
        String secretName = "gcp_service_account_key";
        GetSecretValueResponse response = getSecretValue(secretName);
        return response.secretString(); // Assume JSON-formatted service account key
    }

    // Azure Blob Storage Client Configuration
    @Bean
    public BlobServiceClient azureBlobServiceClient() {
        String connectionString = getAzureConnectionString();
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    private String getAzureConnectionString() {
        String secretName = "azure_blob_connection_string";
        GetSecretValueResponse response = getSecretValue(secretName);
        return response.secretString(); // Connection string stored directly in Secret Manager
    }

    // IBM Cloud Object Storage Client Configuration
    @Bean
    public AmazonS3 ibmCloudObjectStorageClient() {
        String apiKey = getIbmApiKey();
        String serviceInstanceId = getIbmServiceInstanceId();
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain()) // Use AWS SDK's default credentials provider chain
                .withRegion("us-south") // Specify your IBM Cloud region
                .build();
    }

    private String getIbmApiKey() {
        String secretName = "ibm_cos_api_key";
        GetSecretValueResponse response = getSecretValue(secretName);
        return response.secretString();
    }

    private String getIbmServiceInstanceId() {
        String secretName = "ibm_cos_service_instance_id";
        GetSecretValueResponse response = getSecretValue(secretName);
        return response.secretString();
    }

    // Helper method to get secret values from AWS Secrets Manager
    private GetSecretValueResponse getSecretValue(String secretName) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();
            return secretsManagerClient.getSecretValue(request);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Secret not found: " + secretName, e);
        } catch (InvalidRequestException | DecryptionFailureException e) {
            throw new RuntimeException("Error retrieving secret: " + secretName, e);
        }
    }
}