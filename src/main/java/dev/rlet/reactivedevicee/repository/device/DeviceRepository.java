package dev.rlet.reactivedevicee.repository.device;

import dev.rlet.reactivedevicee.entity.Device;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface DeviceRepository extends R2dbcRepository<Device, Long>, CustomDeviceRepository {
  Mono<Device> findById(Long id);
}
