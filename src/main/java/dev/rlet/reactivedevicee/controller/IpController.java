package dev.rlet.reactivedevicee.controller;

import dev.rlet.reactivedevicee.api.DefaultApi;
import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import dev.rlet.reactivedevicee.service.IpAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class IpController implements DefaultApi {

  private final IpAddressService ipAddressService;

  @Override
  public Mono<ResponseEntity<IpAddressResponse>> createIpAddress(
      Mono<IpAddressRequest> ipAddress, ServerWebExchange exchange) {
    return ipAddress
        .flatMap(ipAddressService::createIpAddress)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  @Override
  public Mono<ResponseEntity<Flux<IpAddressResponse>>> listIpAddresses(ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(ipAddressService.listIpAddresses()))
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
}
