package com.ahesan.ecommerce.user_service.mapper;

import com.ahesan.ecommerce.user_service.dto.CreateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UserResponse;
import com.ahesan.ecommerce.user_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    User toEntity(CreateUserRequest request);

}
