package org.example.usersapi.rest.exception;

import java.util.Map;

public record ErrorValidationResponse(int statusCode, Map<String, String> message) {
}
