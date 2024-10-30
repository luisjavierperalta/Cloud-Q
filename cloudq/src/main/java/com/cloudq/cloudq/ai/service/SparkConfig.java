package com.cloudq.cloudq.ai.service;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("YourAppName")
                .master("local[*]") // Change to your cluster URL if using a cluster
                .getOrCreate();
    }
}
