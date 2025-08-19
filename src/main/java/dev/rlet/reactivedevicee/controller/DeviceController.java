package dev.rlet.reactivedevicee.controller;

import dev.rlet.reactivedevicee.api.DevicesApi;
import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class DeviceController implements DevicesApi {

  private final DeviceService deviceService;

  @Override
  public Mono<ResponseEntity<DeviceResponse>> createDevice(
      Mono<DeviceCreateRequest> deviceCreateRequest, ServerWebExchange exchange) {
    return deviceCreateRequest
        .flatMap(deviceService::createDevice)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  @Override
  public Mono<ResponseEntity<Flux<DeviceResponse>>> getAllDevices(
      Integer page, Integer size, ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(deviceService.getAllDevices(page, size)))
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @Override
  public Mono<ResponseEntity<DeviceResponse>> getDevice(Long id, ServerWebExchange exchange) {
    return deviceService
        .getDevice(id)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @Override
  public Mono<ResponseEntity<DeviceResponse>> updateDevice(
      Long id, Mono<DeviceUpdateRequest> deviceUpdateRequest, ServerWebExchange exchange) {
    return deviceUpdateRequest
        .flatMap(request -> deviceService.updateDevice(id, request))
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
}
