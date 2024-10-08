package org.example.usersread.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usersread.application.messaging.UserEventsListener;
import org.example.usersread.application.service.UserCommand;
import org.example.usersread.application.service.UserService;
import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.messaging.dto.UserMessageDto;
import org.example.usersread.infrastructure.messaging.mapper.UserMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserEventsKafkaListener implements UserEventsListener {

    private static final String USERS_UPDATE_TOPIC = "USERS_UPDATE";
    private static final String USERS_DELETE_TOPIC = "USERS_DELETE";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsKafkaListener.class);

    private final UserMessageMapper userMessageMapper;
    private final ObjectMapper objectMapper;
    private final UserCommand userCommand;

    @Autowired
    public UserEventsKafkaListener(ObjectMapper objectMapper,
                                   UserMessageMapper userMessageMapper,
                                   UserCommand userCommand) {
        this.objectMapper = objectMapper;
        this.userMessageMapper = userMessageMapper;
        this.userCommand = userCommand;
    }

    private Optional<UserMessageDto> getUser(String data) {
        Optional<UserMessageDto> user;
        try {
            user = Optional.of(objectMapper.readValue(data, UserMessageDto.class));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error generating json message {} ", data, e);
            user = Optional.empty();
        }
        return user;
    }

    @Override
    @KafkaListener(id = "updatemessage", topics = USERS_UPDATE_TOPIC, clientIdPrefix = "myClientId")
    public void listenUpdateMessage(String data) {
        getUser(data).ifPresent(userMessageDto -> {
            User user = userMessageMapper.toDomain(userMessageDto);
            userCommand.saveOrUpdate(user);
            LOGGER.info("User updated: {}", user);
        });
    }

    @KafkaListener(id = "deletemessage", topics = USERS_DELETE_TOPIC, clientIdPrefix = "myClientId")
    @Override
    public void listenDeleteMessage(String username) {
        userCommand.delete(username);
    }
}
