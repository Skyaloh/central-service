package com.sk.central.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CentralService {

    private static final int TEMP_THRESHOLD = 35;
    private static final int HUMIDITY_THRESHOLD = 50;

    private Logger log = LoggerFactory.getLogger(CentralService.class);

    @KafkaListener(topics = "sensor.temperature", groupId = "central-monitoring-group")
    public void handleTemperatureData(ConsumerRecord<String, String> record) {
        processSensorData(record.value(), "temperature");
    }

    @KafkaListener(topics = "sensor.humidity", groupId = "central-monitoring-group")
    public void handleHumidityData(ConsumerRecord<String, String> record) {
        processSensorData(record.value(), "humidity");
    }

    private void processSensorData(String data, String sensorType) {
        String[] parts = data.split("; ");
        String sensorId = parts[0].split("=")[1];
        int value = Integer.parseInt(parts[1].split("=")[1]);

        log.info("Received {} data: {}", sensorType, data);

        boolean isThresholdExceeded = (sensorType.equals("temperature") && value > TEMP_THRESHOLD) ||
                (sensorType.equals("humidity") && value > HUMIDITY_THRESHOLD);

        if (isThresholdExceeded) {
            String alarmMessage = "ALARM: " + sensorType.toUpperCase() + " sensor " + sensorId +
                    " exceeded threshold with value " + value;
            log.error(alarmMessage);
        } else {
            String message = sensorType.toUpperCase() + " sensor " + sensorId + " value is within threshold: " + value;
            log.info(message);
        }
    }

    public void setLogger(Logger log) {
        this.log = log;
    }

}
