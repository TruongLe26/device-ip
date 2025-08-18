package dev.rlet.devicee.controller;

import dev.rlet.devicee.api.DefaultApi;
import dev.rlet.devicee.dto.DeviceResponse;
import dev.rlet.devicee.dto.DeviceUpdateRequest;
import dev.rlet.devicee.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeviceController implements DefaultApi {

  private final DeviceService deviceService;

  @Override
  public ResponseEntity<DeviceResponse> updateDevice(
      Long id, DeviceUpdateRequest deviceUpdateRequest) {
    DeviceResponse response = deviceService.updateDevice(id, deviceUpdateRequest);
    return ResponseEntity.ok(response);
  }
}
