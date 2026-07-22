package org.example.users.infrastructure.rest.resources;

import jakarta.validation.Valid;
import org.example.users.application.service.UserService;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.rest.dto.UserDto;
import org.example.users.infrastructure.rest.mapper.UserMapper;
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

import java.net.URI;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserResources {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserResources(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<Void> saveUser(@Valid @RequestBody UserDto userDto) {
        User saved = userService.saveUser(userMapper.toDomain(userDto));
        return ResponseEntity.created(URI.create("/api/users/" + saved.username())).build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUser(@PathVariable("username") String username, @Valid @RequestBody UserDto userDto) {
        userService.updateUser(username, userMapper.toDomain(userDto));
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate/{number}")
    public ResponseEntity<List<UserDto>> generateUsers(@PathVariable Integer number) {
        List<User> users =  userService.generateUsers(Optional.ofNullable(number).orElse(1));
        return new ResponseEntity<>(userMapper.toDto(users),
                HttpStatus.OK);
    }
}
