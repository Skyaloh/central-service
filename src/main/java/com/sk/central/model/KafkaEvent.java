package com.sk.central.model;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;
import java.time.Instant;

@Getter
@ToString
public class KafkaEvent extends ApplicationEvent {

    private final String timeStamp;

    @NotNull
    private final String eventName;

    @NotNull
    private final transient Object payload;


    public KafkaEvent(Object source, String eventName, Object payload) {
        super(source);
        this.eventName = eventName;
        this.timeStamp = Instant.now().toString();
        this.payload = payload;
    }
}