package dev.rlet.reactivedevicee.util;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
  DeviceResponse toResponse(Device device);

  Device toCreateEntity(DeviceCreateRequest request);

  @Mapping(target = "id", source = "id")
  Device toUpdateEntity(Long id, DeviceUpdateRequest request);
}
