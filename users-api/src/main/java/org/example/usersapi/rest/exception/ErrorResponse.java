package org.example.usersapi.rest.exception;

public record ErrorResponse(int statusCode, String message) { }
