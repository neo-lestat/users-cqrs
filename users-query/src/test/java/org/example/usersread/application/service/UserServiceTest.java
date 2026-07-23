package org.example.usersread.application.service;

import org.example.usersread.application.repository.UserReadRepository;
import org.example.usersread.domain.model.PagedResult;
import org.example.usersread.domain.model.User;
import org.example.usersread.domain.model.UserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserReadRepository userReadRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserReturnsUser() {
        User user = buildUser();
        when(userReadRepository.findByUsername(any(String.class))).thenReturn(user);
        User result = userService.getUser("test");
        assertEquals(user, result);
    }

    @Test
    void testGetUsersReturnsPagedResult() {
        User user = buildUser();
        PagedResult<User> pagedResult = new PagedResult<>(Collections.singletonList(user), 1, 1);
        when(userReadRepository.findAll(anyInt(), anyInt())).thenReturn(pagedResult);
        PagedResult<User> result = userService.getUsers(0, 1);
        assertEquals(pagedResult, result);
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
