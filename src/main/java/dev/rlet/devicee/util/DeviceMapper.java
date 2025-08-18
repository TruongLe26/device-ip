package dev.rlet.devicee.util;

import dev.rlet.devicee.dto.DeviceResponse;
import dev.rlet.devicee.dto.DeviceUpdateRequest;
import dev.rlet.devicee.entity.devices.Device;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {
  void updateDeviceFromRequest(DeviceUpdateRequest request, @MappingTarget Device device);

  DeviceResponse toResponse(Device device);
}
