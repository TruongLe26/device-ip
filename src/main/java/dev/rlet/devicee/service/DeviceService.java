package dev.rlet.devicee.service;

import dev.rlet.devicee.dto.DeviceResponse;
import dev.rlet.devicee.dto.DeviceUpdateRequest;

public interface DeviceService {
  DeviceResponse updateDevice(Long id, DeviceUpdateRequest request);
}
