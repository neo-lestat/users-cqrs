package org.example.users.infrastructure.rest.exception;

public record ErrorResponse(int statusCode, String message) { }
