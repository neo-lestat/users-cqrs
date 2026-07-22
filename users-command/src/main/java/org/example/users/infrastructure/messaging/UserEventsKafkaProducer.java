package org.example.users.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.users.application.messaging.UserEventsProducer;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.messaging.dto.UserMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class UserEventsKafkaProducer implements UserEventsProducer {

    private static final String USERS_UPDATE_TOPIC = "USERS_UPDATE";
    private static final String USERS_DELETE_TOPIC = "USERS_DELETE";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserEventsKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendCreatedMessage(User user) {
        toJson(buildDto(user, "CREATED")).ifPresent(msg -> sendKafkaMessage(USERS_UPDATE_TOPIC, msg));
    }

    @Override
    public void sendUpdatedMessage(User user) {
        toJson(buildDto(user, "UPDATED")).ifPresent(msg -> sendKafkaMessage(USERS_UPDATE_TOPIC, msg));
    }

    @Override
    public void sendDeleteMessage(String username) {
        sendKafkaMessage(USERS_DELETE_TOPIC, username);
    }

    @Override
    public void sendCreatedMessage(List<User> userList) {
        userList.stream()
                .map(u -> toJson(buildDto(u, "CREATED")))
                .flatMap(Optional::stream)
                .forEach(msg -> sendKafkaMessage(USERS_UPDATE_TOPIC, msg));
    }

    private UserMessageDto buildDto(User user, String eventType) {
        return new UserMessageDto(
                UUID.randomUUID().toString(),
                eventType,
                Instant.now().toString(),
                user.username(), user.name(), user.email(), user.gender(),
                user.pictureUrl(), user.country(), user.state(), user.city());
    }

    private Optional<String> toJson(UserMessageDto dto) {
        try {
            return Optional.of(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing event for user {}", dto.username(), e);
            return Optional.empty();
        }
    }

    private void sendKafkaMessage(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, "userkey", message);
        future.whenComplete((result, ex) -> Optional.ofNullable(ex).ifPresentOrElse(
                e -> LOGGER.error("Unable to send message=[" + message + "] due to : " + ex.getMessage(), e),
                () -> LOGGER.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]")
        ));
    }

}
