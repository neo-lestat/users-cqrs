package org.example.usersread.infrastructure.messaging.mapper;

import org.example.usersread.domain.model.User;
import org.example.usersread.infrastructure.messaging.dto.UserMessageDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMessageMapper {

    List<UserMessageDto> toDto(List<User> userList);

    UserMessageDto toDto(User user);

    User toDomain(UserMessageDto userMessageDto);
}
