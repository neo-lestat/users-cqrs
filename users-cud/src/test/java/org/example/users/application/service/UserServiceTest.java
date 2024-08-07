package org.example.users.application.service;

import org.example.users.application.messaging.UserEventsProducer;
import org.example.users.application.randomgenerator.UserGenerator;
import org.example.users.application.repository.UserRepository;
import org.example.users.domain.model.User;
import org.example.users.domain.model.UserBuilder;
import org.example.users.infrastructure.db.exception.UsernameNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGenerator userGenerator;

    @Mock
    private UserEventsProducer userEventsProducer;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSavesAndSendsUpdateMessage() {
        User user = buildUser();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
        verify(userEventsProducer, times(1)).sendUpdateMessage(user);
        assertEquals(user, result);
    }

    @Test
    public void testUpdatesAndSendsUpdateMessage() {
        User user = buildUser();
        String username = "testUser";
        when(userRepository.update(anyString(), any(User.class))).thenReturn(user);

        User result = userService.updateUser(username, user);

        verify(userRepository, times(1)).update(username, user);
        verify(userEventsProducer, times(1)).sendUpdateMessage(user);
        assertEquals(user, result);
    }

    @Test
    public void testDeletesAndSendsDeleteMessage() {
        String username = "testUser";

        userService.deleteUser(username);

        verify(userRepository, times(1)).delete(username);
        verify(userEventsProducer, times(1)).sendDeleteMessage(username);
    }

    @Test
    public void testGeneratesSavesAndSendsUpdateMessage() {
        User user1 = buildUser();
        User user2 = buildUser();
        List<User> users = Arrays.asList(user1, user2);
        when(userGenerator.generate(anyInt())).thenReturn(users);
        when(userRepository.save(anyList())).thenReturn(users);

        List<User> result = userService.generateUsers(2);

        verify(userGenerator, times(1)).generate(2);
        verify(userRepository, times(1)).save(users);
        verify(userEventsProducer, times(1)).sendUpdateMessage(users);
        assertEquals(users, result);
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