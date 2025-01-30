package com.saysimple.user.handler;

import com.saysimple.user.UserDto;
import com.saysimple.user.UserEventProducer;
import com.saysimple.user.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.saysimple.schema.user.*;
import org.saysimple.util.ModelUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandHandler {

    private final UserPort userPort;
    private final UserEventProducer eventProducer;

    private final Map<Class<?>, Consumer<Object>> eventHandlers = Map.of(
            CreateUserEvent.class, event -> handleCreateUser((CreateUserEvent) event),
            UpdateUserEvent.class, event -> handleUpdateUser((UpdateUserEvent) event),
            DeactivateUserEvent.class, event -> handleDeactivateUser((DeactivateUserEvent) event)
    );

    @KafkaListener(topics = "user-command", groupId = "user-group")
    public void onCommandEvent(ConsumerRecord<String, Event> record) {
        log.info("Received record: {}", record);
        Object event = record.value().getEvent();

        // 이벤트가 존재하면 해당 핸들러 실행, 없으면 경고 로그 출력
        eventHandlers.getOrDefault(event.getClass(), e -> log.warn("Unknown command event: {}", record))
                .accept(event);
    }

    private void handleCreateUser(CreateUserEvent event) {
        try {
            log.info("[CommandConsumer] Creating User: {}", event);
            UserDto dto = ModelUtils.strictMap(event, UserDto.class);
            UserDto user = userPort.create(dto);
            UserCreatedEvent result = ModelUtils.strictMap(user, UserCreatedEvent.class);
            eventProducer.sendResultEvent(result);
        } catch (Exception e) {
            log.error("[CommandConsumer] Error in handleCreateUser: ", e);
        }
    }

    private void handleUpdateUser(UpdateUserEvent event) {
        try {
            log.info("[CommandConsumer] Updating User: {}", event);
            UserDto dto = ModelUtils.map(event, UserDto.class);
            UserDto user = userPort.update(dto);
            UserCreatedEvent result = ModelUtils.map(user, UserCreatedEvent.class);
            eventProducer.sendResultEvent(result);
        } catch (Exception e) {
            log.error("[CommandConsumer] Error in handleUpdateUser: ", e);
        }
    }

    private void handleDeactivateUser(DeactivateUserEvent event) {
        try {
            log.info("[CommandConsumer] Deactivating User: {}", event);
            UserDto user = userPort.deactivate(event.getUserId());
            UserCreatedEvent result = ModelUtils.map(user, UserCreatedEvent.class);
            eventProducer.sendResultEvent(result);
        } catch (Exception e) {
            log.error("[CommandConsumer] Error in handleDeactivateUser: ", e);
        }
    }
}
