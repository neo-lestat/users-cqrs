package org.example.users.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.users.application.messaging.UserEventsProducer;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.messaging.mapper.UserMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class UserEventsKafkaProducer implements UserEventsProducer {

    private static final String USERS_UPDATE_TOPIC = "USERS_UPDATE";
    private static final String USERS_DELETE_TOPIC = "USERS_DELETE";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserMessageMapper userMessageMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserEventsKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   UserMessageMapper userMessageMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userMessageMapper = userMessageMapper;
    }

    @Override
    public void sendUpdateMessage(User user) {
        Optional<String> message = getMessageJson(user);
        message.ifPresent(msg -> sendKafkaMessage(USERS_UPDATE_TOPIC, msg));
    }

    private void sendKafkaMessage(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, "userkey", message);
        future.whenComplete((result, ex) -> Optional.ofNullable(ex).ifPresentOrElse(
                e -> LOGGER.error("Unable to send message=[" + message + "] due to : " + ex.getMessage(), e),
                () -> LOGGER.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]")
        ));
    }

    private Optional<String> getMessageJson(User user) {
        Optional<String> message;
        try {
            message = Optional.ofNullable(objectMapper.writeValueAsString(userMessageMapper.toDto(user)));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error generating json message {} ", user, e);
            message = Optional.empty();
        }
        return message;
    }

    @Override
    public void sendDeleteMessage(String username) {
        sendKafkaMessage(USERS_DELETE_TOPIC, username);
    }

    @Override
    public void sendUpdateMessage(List<User> userList) {
        userList.stream()
                .map(this::getMessageJson)
                .flatMap(Optional::stream)
                .forEach(message -> sendKafkaMessage(USERS_UPDATE_TOPIC, message));
    }

}
