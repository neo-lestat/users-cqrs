package org.example.users.infrastructure.db.exception;

public class UsernameNotAvailableException extends RuntimeException {

    public UsernameNotAvailableException(String message) {
        super(message);
    }


}
