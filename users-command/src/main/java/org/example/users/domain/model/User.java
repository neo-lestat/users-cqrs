package org.example.users.domain.model;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record User(String username, String name, String email, String gender, String pictureUrl,
                   String country, String state, String city) {
    public User(String username, String name, String email) {
        this(username, name, email, null, null, null, null, null);
    }
}
