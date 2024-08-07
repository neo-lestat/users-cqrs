package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserRepository;
import org.example.usersread.domain.model.User;
import org.example.usersread.domain.model.UserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserReturnsUser() {
        User user = buildUser();
        when(userRepository.findByUsername(any(String.class))).thenReturn(user);
        User result = userService.getUser("test");
        assertEquals(user, result);
    }

    @Test
    void testGetUsersReturnsPageOfUsers() {
        User user = buildUser();
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        Page<User> result = userService.getUsers(0, 1);
        assertEquals(userPage, result);
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
