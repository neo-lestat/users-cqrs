package org.example.users.infrastructure.rest.resources;

import jakarta.validation.Valid;
import org.example.users.application.usecase.DeleteUserUseCase;
import org.example.users.application.usecase.GenerateUsersUseCase;
import org.example.users.application.usecase.SaveUserUseCase;
import org.example.users.application.usecase.UpdateUserUseCase;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.rest.dto.UserDto;
import org.example.users.infrastructure.rest.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserResources {

    private final DeleteUserUseCase deleteUserUseCase;
    private final GenerateUsersUseCase generateUsersUseCase;
    private final SaveUserUseCase saveUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserMapper userMapper;

    @Autowired
    public UserResources(DeleteUserUseCase deleteUserUseCase,
                         GenerateUsersUseCase generateUsersUseCase,
                         SaveUserUseCase saveUserUseCase,
                         UpdateUserUseCase updateUserUseCase,
                         UserMapper userMapper) {
        this.deleteUserUseCase = deleteUserUseCase;
        this.generateUsersUseCase = generateUsersUseCase;
        this.saveUserUseCase = saveUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userMapper.toDto(saveUserUseCase.saveUser(userMapper.toDomain(userDto))),
                HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username, @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userMapper.toDto(updateUserUseCase.updateUser(username, userMapper.toDomain(userDto))),
                HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        deleteUserUseCase.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate/{number}")
    public ResponseEntity<List<UserDto>> generateUsers(@PathVariable Integer number) {
        List<User> users =  generateUsersUseCase.generateUsers(Optional.ofNullable(number).orElse(1));
        return new ResponseEntity<>(userMapper.toDto(users),
                HttpStatus.OK);
    }
}
