package org.example.users.application.messaging;

import org.example.users.domain.model.User;

import java.util.List;

public interface UserEventsProducer {

    void sendUpdateMessage(User user);

    void sendDeleteMessage(String username);

    void sendUpdateMessage(List<User> userList);
}
