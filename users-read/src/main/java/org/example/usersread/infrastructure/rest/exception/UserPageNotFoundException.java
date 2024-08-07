package org.example.usersread.infrastructure.rest.exception;

public class UserPageNotFoundException extends RuntimeException {

    public UserPageNotFoundException(String message) {
        super(message);
    }

}
