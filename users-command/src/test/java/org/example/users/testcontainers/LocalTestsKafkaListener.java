package org.example.users.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocalTestsKafkaListener {

    private static final String USERS_UPDATE_TOPIC = "USERS_UPDATE";
    private static final String USERS_DELETE_TOPIC = "USERS_DELETE";

    private final Logger logger = LoggerFactory.getLogger(LocalTestsKafkaListener.class);

    private final List<String> messages;

    public LocalTestsKafkaListener() {
        this.messages = new ArrayList<>();
    }

    public List<String> getMessages() {
        return messages;
    }

    public void clearMessages() {
        messages.clear();
    }

    @KafkaListener(topics = USERS_UPDATE_TOPIC, groupId = "local-test-update-group")
    public void handleUpdate(String event) {
        messages.add(event);
        logger.info("Received message: {}", event);
    }

    @KafkaListener(topics = USERS_DELETE_TOPIC, groupId = "local-test-delete-group")
    public void handleDelete(String event) {
        messages.add(event);
        logger.info("Received message: {}", event);
    }
}
