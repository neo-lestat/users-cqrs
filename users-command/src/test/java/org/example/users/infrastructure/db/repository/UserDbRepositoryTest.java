package org.example.users.infrastructure.db.repository;

import org.example.users.domain.model.User;
import org.example.users.domain.model.UserBuilder;
import org.example.users.infrastructure.db.entity.UserEntity;
import org.example.users.infrastructure.db.exception.UserNotFoundException;
import org.example.users.infrastructure.db.exception.UsernameNotAvailableException;

import org.example.users.infrastructure.db.mapper.UserEntityMapper;
import org.example.users.infrastructure.db.mapper.UserEntityMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserDbRepositoryTest {

    @Mock
    private SpringDataUserRepository springDataUserRepository;

    private UserEntityMapper userMapper;
    private UserDbRepository userDbRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userMapper = new UserEntityMapperImpl();
        userDbRepository = new UserDbRepository(springDataUserRepository, userMapper);
    }

    @Test
    public void testSaveUserWhenUserExistsThrowsException() {
        User user = buildUser();
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.of(buildEntityFromDomain(user)));
        assertThrows(UsernameNotAvailableException.class, () -> userDbRepository.save(user));
    }

    @Test
    public void testSaveUserWhenUsernameDoesNotExist() {
        User user = buildUser();
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.empty());
        when(springDataUserRepository.save(any(UserEntity.class))).thenReturn(buildEntityFromDomain(user));

        User result = userDbRepository.save(user);
        assertNotNull(result);
        assertEquals(user, result);
        verify(springDataUserRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testSaveUserListWhenUsernameDoesNotExist() {
        User user = buildUser();
        List<User> users = List.of(user, UserBuilder.builder(user).username("test2").build());
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.empty());
        when(springDataUserRepository.saveAll(any(List.class))).
                thenReturn(List.of(buildEntityFromDomain(users.get(0)), buildEntityFromDomain(users.get(1))));
        List<User> result = userDbRepository.save(users);
        assertNotNull(result);
        assertEquals(users.size(), result.size());
        assertEquals(users.get(0), result.get(0));
        assertEquals(users.get(1), result.get(1));
        verify(springDataUserRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void testSaveUserListWhenAUsernameExist() {
        User user = buildUser();
        List<User> users = List.of(user, UserBuilder.builder(user).build());
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.empty());
        when(springDataUserRepository.saveAll(any(List.class))).
                thenReturn(List.of(buildEntityFromDomain(users.get(0))));
        List<User> result = userDbRepository.save(users);
        //todo use capture to verify the list of users that saveAll receives
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(users.get(0), result.get(0));
        verify(springDataUserRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void testUpdateUserWhenUserDoesNotExistThrowsException() {
        User user = buildUser();
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userDbRepository.update(user.username(), user));
    }

    @Test
    public void testUpdateUserWhenUserExists() {
        User user = buildUser();
        UserEntity userEntity = buildEntityFromDomain(user);
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.of(userEntity));
        when(springDataUserRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        User result = userDbRepository.update(user.username(), user);
        assertNotNull(result);
        assertEquals(user, result);
        verify(springDataUserRepository, times(1)).save(userEntity);
    }

    @Test
    public void testDeleteUserWhenUserDoesNotExistThrowsException() {
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userDbRepository.delete("nonExistingUser"));
    }

    @Test
    public void testDeleteUserWhenUserExists() {
        UserEntity userEntity = new UserEntity();
        when(springDataUserRepository.findById(anyString())).thenReturn(Optional.of(userEntity));
        userDbRepository.delete("existingUser");
        verify(springDataUserRepository, times(1)).delete(userEntity);
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

    private UserEntity buildEntityFromDomain(User user) {
        return userMapper.toDbo(user);
    }
}
