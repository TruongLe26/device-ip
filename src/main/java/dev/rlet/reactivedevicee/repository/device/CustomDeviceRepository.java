package dev.rlet.reactivedevicee.repository.device;

import dev.rlet.reactivedevicee.entity.Device;
import reactor.core.publisher.Flux;

public interface CustomDeviceRepository {
  Flux<Device> findDevices(int pageNumber, int pageSize);
}
