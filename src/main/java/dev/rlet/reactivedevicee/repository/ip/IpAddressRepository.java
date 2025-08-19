package dev.rlet.reactivedevicee.repository.ip;

import dev.rlet.reactivedevicee.entity.IpAddress;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface IpAddressRepository
    extends R2dbcRepository<IpAddress, Long>, CustomIpAddressRepository {
  Mono<IpAddress> findByImsi(String imsi);
}
