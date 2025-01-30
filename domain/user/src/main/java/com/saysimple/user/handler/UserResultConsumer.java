package com.saysimple.user.handler;

import com.saysimple.events.EventEntity;
import com.saysimple.events.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.saysimple.schema.user.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserResultConsumer {

    private final EventRepository eventRepository;

    private final Map<Class<?>, Consumer<Object>> eventHandlers = Map.of(
            UserCreatedEvent.class, event -> handleUserCreated((UserCreatedEvent) event),
            UserUpdatedEvent.class, event -> handleUserUpdated((UserUpdatedEvent) event),
            UserDeactivatedEvent.class, event -> handleUserDeactivated((UserDeactivatedEvent) event)
    );

    @KafkaListener(topics = "user-result", groupId = "user-group")
    public void onresultEvent(ConsumerRecord<String, Event> record) {
        log.info("Received record: {}", record);
        Object event = record.value().getEvent();

        // 이벤트가 존재하면 해당 핸들러 실행, 없으면 경고 로그 출력
        eventHandlers.getOrDefault(event.getClass(), e -> log.warn("Unknown result event: {}", record))
                .accept(event);
    }

    private void handleUserCreated(UserCreatedEvent evt) {
        log.info("[ResultConsumer] UserCreated: {}", evt);
        EventEntity entity = EventEntity.builder()
                .eventType("UserCreatedEvent")
                .payload(evt.toString())
                .eventTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        eventRepository.save(entity);
    }

    private void handleUserUpdated(UserUpdatedEvent evt) {
        log.info("[ResultConsumer] UserUpdated: {}", evt);
        EventEntity entity = EventEntity.builder()
                .eventType("UserUpdatedEvent")
                .payload(evt.toString())
                .eventTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        eventRepository.save(entity);
    }

    private void handleUserDeactivated(UserDeactivatedEvent evt) {
        log.info("[ResultConsumer] UserDeleted: {}", evt);
        EventEntity entity = EventEntity.builder()
                .eventType("UserDeletedEvent")
                .payload("UserId=" + evt.getUserId())
                .eventTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        eventRepository.save(entity);
    }
}
