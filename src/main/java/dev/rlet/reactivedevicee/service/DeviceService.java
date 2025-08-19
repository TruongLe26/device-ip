package dev.rlet.reactivedevicee.service;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceService {
  Mono<DeviceResponse> createDevice(DeviceCreateRequest request);

  Mono<DeviceResponse> updateDevice(Long id, DeviceUpdateRequest request);

  Mono<DeviceResponse> getDevice(Long id);

  Flux<DeviceResponse> getAllDevices(Integer page, Integer size);
}
