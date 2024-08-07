package org.example.users.infrastructure.randomgenerator;

import org.example.users.application.randomgenerator.UserGenerator;
import org.example.users.domain.model.User;
import org.example.users.infrastructure.randomgenerator.dto.Root;
import org.example.users.infrastructure.randomgenerator.mapper.RootMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserRandomGeneratorClient implements UserGenerator {

    private final RestClient restClient;
    private RootMapper rootMapper;

    @Autowired
    public UserRandomGeneratorClient(RootMapper rootMapper) {
        this.restClient = RestClient.create();
        this.rootMapper = rootMapper;
    }

    @Override
    public List<User> generate(int number) {
        Root root = restClient.get()
                .uri(String.format("https://randomuser.me/api/?results=%d", number))
                .retrieve()
                .body(Root.class);
        return rootMapper.toDomain(root);
    }

}
