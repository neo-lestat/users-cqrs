package org.example.usersapi.clients.usersread;

import org.example.usersapi.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "usersReadClient", url = "${users.read.service.url}/api/users")
public interface UsersReadFeignClient {

    @GetMapping(params = {"page", "size"})
    List<UserDto> getUsers(@RequestParam("page") int page, @RequestParam("size") int size);

    @GetMapping("/{username}")
    UserDto getUser(@PathVariable("username") String username);

    /*@GetMapping("/tree")
    UserDto getUserTree();*/

}
