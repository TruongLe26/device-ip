package dev.rlet.reactivedevicee.service;

import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IpAddressService {
  Mono<IpAddressResponse> createIpAddress(IpAddressRequest request);

  Flux<IpAddressResponse> listIpAddresses();

  Mono<Void> updateIpAddress(String imsi, String newIp);
}
