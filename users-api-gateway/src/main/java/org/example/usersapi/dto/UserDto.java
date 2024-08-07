package org.example.usersapi.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank(message = "Username is mandatory") String username,
                      @NotBlank(message = "Name is mandatory") String name,
                      @NotBlank(message = "Email is mandatory") String email,
                      @NotBlank(message = "Gender is mandatory") String gender,
                      String pictureUrl,
                      @NotBlank(message = "Country is mandatory") String country,
                      @NotBlank(message = "State is mandatory") String state,
                      @NotBlank(message = "City is mandatory") String city) {

}
