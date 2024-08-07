package org.example.users.infrastructure.randomgenerator.mapper;

import org.example.users.domain.model.User;
import org.example.users.infrastructure.randomgenerator.dto.Result;
import org.example.users.infrastructure.randomgenerator.dto.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RootMapper {

    public List<User> toDomain(Root root) {
        List<User> userList = new ArrayList<>();
        root.getResults()
                .forEach(result -> userList.add(toDomain(result)));
        return userList;
    }

    public User toDomain(Result result) {
        String username = result.getName().getFirst().toLowerCase() + "." + result.getName().getLast().toLowerCase();
        String name = result.getName().getFirst() + " " + result.getName().getLast();
        String email = result.getEmail();
        String gender = result.getGender();
        String pictureUrl = result.getPicture().getLarge();
        String country = result.getLocation().getCountry();
        String state = result.getLocation().getState();
        String city = result.getLocation().getCity();
        return new User(username, name, email, gender, pictureUrl, country, state, city);
    }

}
