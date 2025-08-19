package dev.rlet.reactivedevicee.util;

import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import dev.rlet.reactivedevicee.entity.IpAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IpAddressMapper {
  IpAddressResponse toResponse(IpAddress ipAddress);

  IpAddress toEntity(IpAddressRequest request);
}
