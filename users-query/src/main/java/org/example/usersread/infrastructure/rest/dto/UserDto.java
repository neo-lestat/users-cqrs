package org.example.usersread.infrastructure.rest.dto;


public record UserDto(String username, String name,
                      String email, String gender, String pictureUrl,
                      String country, String state, String city) {

}
