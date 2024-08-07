package org.example.users.infrastructure.messaging.mapper;

import org.example.users.domain.model.User;
import org.example.users.infrastructure.messaging.dto.UserMessageDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMessageMapper {

    List<UserMessageDto> toDto(List<User> userList);

    UserMessageDto toDto(User user);
}
