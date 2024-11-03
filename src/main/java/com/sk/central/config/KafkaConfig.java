package com.sk.central.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaConfig {
    private final Environment env;

    public KafkaConfig(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkKafkaConnection() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap-servers");
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        log.info("Connecting to Kafka on {}", bootstrapServers);
        try (AdminClient adminClient = AdminClient.create(props)) {
            adminClient.describeCluster().clusterId().get();
            log.info("Connected to Kafka successfully :)!");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to connect to Kafka. Application will not start", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }
}
