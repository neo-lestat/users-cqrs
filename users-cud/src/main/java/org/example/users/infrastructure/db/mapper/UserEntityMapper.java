package org.example.users.infrastructure.db.mapper;

import org.example.users.domain.model.User;
import org.example.users.infrastructure.db.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    User toDomain(UserEntity userEntity);

    List<User> toDomain(List<UserEntity> userEntityList);

    UserEntity toDbo(User user);

    List<UserEntity> toDbo(List<User> userList);


}
