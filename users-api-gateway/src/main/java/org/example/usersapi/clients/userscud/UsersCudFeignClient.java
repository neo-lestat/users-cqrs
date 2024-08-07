package org.example.usersapi.clients.userscud;

import org.example.usersapi.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "usersCudClient", url = "${users.cud.service.url}/api/users")
public interface UsersCudFeignClient {

    @PostMapping
    UserDto saveUser(@RequestBody UserDto userDto);

    @PutMapping("/{username}")
    UserDto updateUser(@PathVariable("username") String username, @RequestBody UserDto userDto);

    @DeleteMapping("/{username}")
    String deleteUser(@PathVariable("username") String username);

    @GetMapping("/generate/{number}")
    List<UserDto> generateUsers(@PathVariable Integer number);

}
