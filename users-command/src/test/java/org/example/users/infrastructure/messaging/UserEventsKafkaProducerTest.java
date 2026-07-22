package org.example.users.infrastructure.messaging;

import tools.jackson.databind.ObjectMapper;
import org.example.users.domain.model.User;
import org.example.users.domain.model.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.core.JacksonException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserEventsKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CompletableFuture future;

    private UserEventsKafkaProducer userEventsKafkaProducer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userEventsKafkaProducer = new UserEventsKafkaProducer(kafkaTemplate, objectMapper);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);
    }

    @Test
    public void testSendCreatedMessageWhenUserIsValid() throws JacksonException {
        User user = buildUser();
        when(objectMapper.writeValueAsString(any())).thenReturn("message");
        userEventsKafkaProducer.sendCreatedMessage(user);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendCreatedMessageWhenJsonProcessingFailsDoesNotSendMessage() throws JacksonException {
        User user = buildUser();
        when(objectMapper.writeValueAsString(any())).thenThrow(JacksonException.class);
        userEventsKafkaProducer.sendCreatedMessage(user);
        verify(kafkaTemplate, times(0)).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendDeleteMessage() {
        String username = "testUser";
        userEventsKafkaProducer.sendDeleteMessage(username);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), eq(username));
    }

    @Test
    public void testSendCreatedMessageWhenUserListIsValidSendsMessages() throws JacksonException {
        List<User> users = Arrays.asList(buildUser(), buildUser());
        when(objectMapper.writeValueAsString(any())).thenReturn("message");
        userEventsKafkaProducer.sendCreatedMessage(users);
        verify(kafkaTemplate, times(users.size())).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendCreatedMessageWhenUserListContainsInvalidUserDoesNotSendAllMessages() throws JacksonException {
        List<User> users = Arrays.asList(buildUser(), UserBuilder.builder(buildUser()).username("test2").build());
        when(objectMapper.writeValueAsString(any())).thenReturn("message").thenThrow(JacksonException.class);
        userEventsKafkaProducer.sendCreatedMessage(users);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }

    private User buildUser() {
        return UserBuilder.builder()
                .username("test")
                .name("test test")
                .email("test@test")
                .gender("male")
                .pictureUrl("test")
                .build();
    }


}
