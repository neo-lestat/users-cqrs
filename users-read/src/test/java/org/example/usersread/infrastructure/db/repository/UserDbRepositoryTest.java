package org.example.usersread.infrastructure.db.repository;

import org.example.usersread.domain.model.User;

import org.example.usersread.domain.model.UserBuilder;
import org.example.usersread.infrastructure.db.entity.UserEntity;
import org.example.usersread.infrastructure.db.exception.UserNotFoundException;
import org.example.usersread.infrastructure.db.exception.UsernameNotAvailableException;
import org.example.usersread.infrastructure.db.mapper.UserEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class UserDbRepositoryTest {

    @Mock
    private SpringDataUserRepository springDataUserRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserDbRepository userDbRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUsernameExistReturnsTrue() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.of(new UserEntity()));
        boolean result = userDbRepository.usernameExist("test");
        assertTrue(result);
    }

    @Test
    void testUsernameDoesNotExistReturnsFalse() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        boolean result = userDbRepository.usernameExist("test");
        assertFalse(result);
    }

    @Test
    void testFindByUsernameReturnsUser() {
        UserEntity userEntity = new UserEntity();
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.of(userEntity));
        when(userEntityMapper.toDomain(any(UserEntity.class))).thenReturn(buildUser());
        User result = userDbRepository.findByUsername("test");
        assertNotNull(result);
    }

    @Test
    void testFindByUsernameThrowsUserNotFoundException() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userDbRepository.findByUsername("test"));
    }

    @Test
    void testFindAllReturnsPageOfUsers() {
        Page<UserEntity> pageUserEntity = new PageImpl<>(Collections.singletonList(new UserEntity()));
        when(springDataUserRepository.findAll(any(PageRequest.class))).thenReturn(pageUserEntity);
        when(userEntityMapper.toDomain(anyList())).thenReturn(Collections.singletonList(buildUser()));
        Page<User> result = userDbRepository.findAll(PageRequest.of(0, 1));
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSaveWhenUsernameDoesNotExist() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        when(springDataUserRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());
        when(userEntityMapper.toDbo(any(User.class))).thenReturn(new UserEntity());
        when(userEntityMapper.toDomain(any(UserEntity.class))).thenReturn(buildUser());
        User result = userDbRepository.save(buildUser());
        assertNotNull(result);
    }

    @Test
    void testSaveWhenUsernameExistsThrowsUsernameNotAvailableException() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.of(new UserEntity()));
        assertThrows(UsernameNotAvailableException.class, () -> userDbRepository.save(buildUser()));
    }

    @Test
    void testUpdateWhenUsernameExists() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("test");
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.of(userEntity));
        when(springDataUserRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userEntityMapper.toDbo(any(User.class))).thenReturn(new UserEntity());
        when(userEntityMapper.toDomain(any(UserEntity.class))).thenReturn(buildUser());
        User result = userDbRepository.update("test", buildUser());
        assertNotNull(result);
    }

    @Test
    void testUpdateWhenUsernameDoesNotExistThrowsUserNotFoundException() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userDbRepository.update("test", buildUser()));
    }

    @Test
    void testDeleteWhenUsernameExists() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.of(new UserEntity()));
        assertDoesNotThrow(() -> userDbRepository.delete("test"));
    }

    @Test
    void testDeleteWhenUsernameDoesNotExist() {
        when(springDataUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userDbRepository.delete("test"));
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

