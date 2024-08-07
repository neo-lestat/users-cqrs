package org.example.users.infrastructure.randomgenerator.dto;

import lombok.Data;

import java.time.ZonedDateTime;
@Data
public class Dob {
    private ZonedDateTime date;
    private int age;
}
