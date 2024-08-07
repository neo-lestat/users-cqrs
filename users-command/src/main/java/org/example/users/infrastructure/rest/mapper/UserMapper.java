package org.example.users.infrastructure.rest.mapper;

import org.example.users.domain.model.User;
import org.example.users.infrastructure.rest.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> toDto(List<User> userList);

    UserDto toDto(User user);

    User toDomain(UserDto userDto);
}
