package org.example.usersread.infrastructure.rest.resources;

import org.example.usersread.application.usecase.GetUserUseCase;
import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.rest.dto.UserDto;
import org.example.usersread.infrastructure.rest.exception.UserPageNotFoundException;
import org.example.usersread.infrastructure.rest.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserResources {

    private final GetUserUseCase getUserUseCase;
    private final UserMapper userMapper;

    @Autowired
    public UserResources(GetUserUseCase getUserUseCase, UserMapper userMapper) {
        this.getUserUseCase = getUserUseCase;
        this.userMapper = userMapper;
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        Page<User> pageUser = getUserUseCase.getUsers(page, size);
        if (page > pageUser.getTotalPages()) {
            throw new UserPageNotFoundException("Page not found");
        }
        return new ResponseEntity<>(userMapper.toDto(pageUser.getContent()),
                HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(userMapper.toDto(getUserUseCase.getUser(username)),
                HttpStatus.OK);
    }

    @GetMapping("/tree")
    public ResponseEntity<Map<String, Map<String, Map<String, List<UserDto>>>>> getUserTree() {
        //return ResponseEntity.ok().build();
        Page<User> pageUser = getUserUseCase.getUsers(0, 100);
        List<UserDto> userDtoList = userMapper.toDto(pageUser.getContent());
        Map<String, Map<String, Map<String, List<UserDto>>>> maps = new HashMap<>();
        userDtoList.forEach(userDto -> {
            maps.computeIfAbsent(userDto.country(), k -> new HashMap<>())
                    .computeIfAbsent(userDto.state(), k -> new HashMap<>())
                    .computeIfAbsent(userDto.city(), k -> new ArrayList<>())
                    .add(userDto);
        });
        return new ResponseEntity<>(maps,
                HttpStatus.OK);
    }
}
