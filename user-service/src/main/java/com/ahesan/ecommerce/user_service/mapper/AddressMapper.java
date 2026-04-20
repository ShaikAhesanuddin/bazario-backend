package com.ahesan.ecommerce.user_service.mapper;

import com.ahesan.ecommerce.user_service.dto.AddressRequest;
import com.ahesan.ecommerce.user_service.dto.AddressResponse;
import com.ahesan.ecommerce.user_service.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "defaultAddress", target = "isDefault")
    AddressResponse toResponse(Address address);

    @Mapping(source = "isDefault", target = "defaultAddress")
    Address toEntity(AddressRequest request);
}
