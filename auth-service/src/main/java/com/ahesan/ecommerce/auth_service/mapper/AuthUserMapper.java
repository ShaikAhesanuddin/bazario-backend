package com.ahesan.ecommerce.auth_service.mapper;

import com.ahesan.ecommerce.auth_service.dto.RegisterRequest;
import com.ahesan.ecommerce.auth_service.entity.AuthUser;
import com.ahesan.ecommerce.auth_service.external.dto.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    AuthUser toEntity(RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    CreateUserRequest toCreateUserRequest(RegisterRequest request);
}
