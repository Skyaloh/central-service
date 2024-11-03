package com.sk.central.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"sensor.temperature", "sensor.humidity"})
class CentralServiceTest {
    @Mock
    private Logger log;

    @InjectMocks
    private CentralService centralService;

    @Captor
    private ArgumentCaptor<String> logCaptor;

    @Test
    void testHandleTemperatureData() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("sensor.temperature", 0, 0L, null, "sensorId=1; value=36");
        centralService.handleTemperatureData(record);
        centralService.setLogger(log);

        verify(log).info(logCaptor.capture(),logCaptor.capture(),logCaptor.capture());
        assertEquals("sensorId=1; value=36", logCaptor.getValue());

        verify(log).error(logCaptor.capture());
        assertEquals("ALARM: TEMPERATURE sensor 1 exceeded threshold with value 36", logCaptor.getValue());
    }

    @Test
    void testHandleHumidityData() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("sensor.humidity", 0, 0L, null, "sensorId=2; value=55");
        centralService.handleHumidityData(record);
        centralService.setLogger(log);

        verify(log,atLeastOnce()).info(logCaptor.capture(),logCaptor.capture(),logCaptor.capture());

        assertEquals("sensorId=2; value=55", logCaptor.getValue());

        verify(log).error(logCaptor.capture());
        assertEquals("ALARM: HUMIDITY sensor 2 exceeded threshold with value 55", logCaptor.getValue());
    }

    @Test
    void testTemperatureWithinThreshold() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("sensor.temperature", 0, 0L, null, "sensorId=1; value=30");
        centralService.handleTemperatureData(record);
        centralService.setLogger(log);


        verify(log).info(logCaptor.capture());
        assertEquals("TEMPERATURE sensor 1 value is within threshold: 30", logCaptor.getValue());
    }

    @Test
    void testHumidityWithinThreshold() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("sensor.humidity", 0, 0L, null, "sensorId=2; value=45");
        centralService.handleHumidityData(record);
        centralService.setLogger(log);

        verify(log).info(logCaptor.capture());
        assertEquals("HUMIDITY sensor 2 value is within threshold: 45", logCaptor.getValue());
    }
}