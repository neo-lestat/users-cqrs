package org.example.users.infrastructure.messaging.dto;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record UserMessageDto(String eventId, String eventType, String occurredAt,
                             String username, String name, String email, String gender, String pictureUrl,
                             String country, String state, String city) {
}
