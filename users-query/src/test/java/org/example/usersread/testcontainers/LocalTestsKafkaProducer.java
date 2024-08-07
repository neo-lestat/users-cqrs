package org.example.usersread.testcontainers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usersread.infrastructure.messaging.dto.UserMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class LocalTestsKafkaProducer {

    private static final String USERS_UPDATE_TOPIC = "USERS_UPDATE";
    private static final String USERS_DELETE_TOPIC = "USERS_DELETE";

    private final Logger logger = LoggerFactory.getLogger(LocalTestsKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendUpdateMessage(UserMessageDto user) {
        Optional<String> message = getMessageJson(user);
        message.ifPresent(msg -> sendKafkaMessage(USERS_UPDATE_TOPIC, msg));
    }

    private void sendKafkaMessage(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, "userkey", message);
        future.whenComplete((result, ex) -> Optional.ofNullable(ex).ifPresentOrElse(
                e -> logger.error("Unable to send message=[" + message + "] due to : " + ex.getMessage(), e),
                () -> logger.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]")
        ));
    }

    private Optional<String> getMessageJson(UserMessageDto user) {
        Optional<String> message;
        try {
            message = Optional.ofNullable(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            logger.error("Error generating json message {} ", user, e);
            message = Optional.empty();
        }
        return message;
    }

    public void sendDeleteMessage(String username) {
        sendKafkaMessage(USERS_DELETE_TOPIC, username);
    }

}
