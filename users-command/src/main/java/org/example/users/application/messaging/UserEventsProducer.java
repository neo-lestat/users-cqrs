package org.example.users.application.messaging;

import org.example.users.domain.model.User;

import java.util.List;

public interface UserEventsProducer {

    void sendCreatedMessage(User user);

    void sendUpdatedMessage(User user);

    void sendDeleteMessage(String username);

    void sendCreatedMessage(List<User> userList);
}
