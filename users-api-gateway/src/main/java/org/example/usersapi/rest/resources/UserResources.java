package org.example.usersapi.rest.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.usersapi.clients.userscud.UsersCudFeignClient;
import org.example.usersapi.clients.usersread.UsersReadFeignClient;
import org.example.usersapi.dto.UserDto;
import org.example.usersapi.rest.exception.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserResources {

    private final UsersCudFeignClient usersCudFeignClient;
    private final UsersReadFeignClient usersReadFeignClient;


    @Autowired
    public UserResources(UsersCudFeignClient usersCudFeignClient,
                         UsersReadFeignClient usersReadFeignClient) {
        this.usersCudFeignClient = usersCudFeignClient;
        this.usersReadFeignClient = usersReadFeignClient;
    }

    @Operation(summary = "Get users with pagination")
    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
        return new ResponseEntity<>(usersReadFeignClient.getUsers(page, size),
                HttpStatus.OK);
    }

    @Operation(summary = "Get user by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Return a user by username"),
        @ApiResponse(responseCode = "404", description = "Not found user by username",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(usersReadFeignClient.getUser(username),
                HttpStatus.OK);
    }

    @Operation(summary = "Get users tree")
    @ApiResponse(responseCode = "200", description = "Return a map with users grouped by country, state and city")
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Map<String, Map<String, List<UserDto>>>>> getUserTree() {
        List<UserDto> userDtoList = usersReadFeignClient.getUsers(0, 100);
        Map<String, Map<String, Map<String, List<UserDto>>>> maps = new HashMap<>();
        userDtoList.forEach(userDto -> {
            maps.computeIfAbsent(userDto.country(), k -> new HashMap<>())
                    .computeIfAbsent(userDto.state(), k -> new HashMap<>())
                    .computeIfAbsent(userDto.city(), k -> new ArrayList<>())
                    .add(userDto);
        });
        return new ResponseEntity<>(maps, HttpStatus.OK);
    }

    @Operation(summary = "Create a user")
    @ApiResponse(responseCode = "200", description = "Return the created user")
    @ApiResponse(responseCode = "400", description = "Bad request",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @PostMapping
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(usersCudFeignClient.saveUser(userDto),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update a user")
    @ApiResponse(responseCode = "202", description = "Return the updated user")
    @ApiResponse(responseCode = "404", description = "Not found user by username",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username, @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(usersCudFeignClient.updateUser(username, userDto),
                HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "200", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found by username",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        usersCudFeignClient.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate random users with randomuser.me api")
    @ApiResponse(responseCode = "200", description = "Return a list of generated users")
    @GetMapping("/generate/{number}")
    public ResponseEntity<List<UserDto>> generateUsers(@PathVariable Integer number) {
        return new ResponseEntity<>(usersCudFeignClient.generateUsers(Optional.ofNullable(number).orElse(1)),
                HttpStatus.OK);
    }
}
