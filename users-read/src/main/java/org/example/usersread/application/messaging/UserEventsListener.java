package org.example.usersread.application.messaging;


public interface UserEventsListener {

    void listenUpdateMessage(String data);

    void listenDeleteMessage(String username);

}
