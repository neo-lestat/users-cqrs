package org.example.usersread.infrastructure.rest.resources;

import org.example.usersread.application.usecase.UserQueryUseCase;
import org.example.usersread.domain.model.PagedResult;
import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.rest.dto.UserDto;
import org.example.usersread.infrastructure.rest.exception.UserPageNotFoundException;
import org.example.usersread.infrastructure.rest.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserResources {

    private final UserQueryUseCase userService;
    private final UserMapper userMapper;

    public UserResources(UserQueryUseCase userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        PagedResult<User> pageUser = userService.getUsers(page, size);
        if (page > pageUser.totalPages()) {
            throw new UserPageNotFoundException("Page not found");
        }
        return new ResponseEntity<>(userMapper.toDto(pageUser.content()), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(userMapper.toDto(userService.getUser(username)), HttpStatus.OK);
    }

    @GetMapping("/tree")
    public ResponseEntity<Map<String, Map<String, Map<String, List<UserDto>>>>> getUserTree() {
        Map<String, Map<String, Map<String, List<User>>>> tree = userService.getTreeUsers();
        Map<String, Map<String, Map<String, List<UserDto>>>> result = new java.util.HashMap<>();
        tree.forEach((country, stateMap) -> {
            Map<String, Map<String, List<UserDto>>> stateDtoMap = new java.util.HashMap<>();
            stateMap.forEach((city, cityMap) -> {
                Map<String, List<UserDto>> cityDtoMap = new java.util.HashMap<>();
                cityMap.forEach((state, users) -> cityDtoMap.put(state, userMapper.toDto(users)));
                stateDtoMap.put(city, cityDtoMap);
            });
            result.put(country, stateDtoMap);
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
