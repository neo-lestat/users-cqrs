package org.example.usersread.infrastructure.rest.mapper;

import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.rest.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> toDto(List<User> userList);

    UserDto toDto(User user);

    User toDomain(UserDto userDto);
}
