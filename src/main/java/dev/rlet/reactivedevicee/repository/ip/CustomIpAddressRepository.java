package dev.rlet.reactivedevicee.repository.ip;

import dev.rlet.reactivedevicee.entity.IpAddress;
import reactor.core.publisher.Mono;

public interface CustomIpAddressRepository {
  Mono<IpAddress> updateIpAddress(IpAddress ipAddress);
}
