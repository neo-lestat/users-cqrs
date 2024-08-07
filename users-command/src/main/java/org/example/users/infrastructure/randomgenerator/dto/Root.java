package org.example.users.infrastructure.randomgenerator.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Root {
    private ArrayList<Result> results;
    private Info info;
}
