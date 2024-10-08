package org.example.users.application.service;

import org.example.users.application.messaging.UserEventsProducer;
import org.example.users.application.randomgenerator.UserGenerator;
import org.example.users.application.repository.UserRepository;
import org.example.users.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserGenerator userGenerator;
    private final UserEventsProducer userEventsProducer;

    public UserService(UserRepository userRepository, UserGenerator userGenerator,
                       UserEventsProducer userEventsProducer) {
        this.userRepository = userRepository;
        this.userGenerator = userGenerator;
        this.userEventsProducer = userEventsProducer;
    }

    public User saveUser(User user) {
        User userSaved = userRepository.save(user);
        userEventsProducer.sendUpdateMessage(userSaved);
        return userSaved;
    }

    public User updateUser(String username, User user) {
        User userUpdated = userRepository.update(username, user);
        userEventsProducer.sendUpdateMessage(userUpdated);
        return userUpdated;
    }

    public void deleteUser(String username) {
        userRepository.delete(username);
        userEventsProducer.sendDeleteMessage(username);
    }

    public List<User> generateUsers(int number) {
        List<User> userGeneratedList = userGenerator.generate(number);
        List<User> userList = userRepository.save(userGeneratedList);
        userEventsProducer.sendUpdateMessage(userList);
        return userList;
    }
}
