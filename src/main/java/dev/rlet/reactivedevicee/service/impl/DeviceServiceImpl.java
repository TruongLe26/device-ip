package dev.rlet.reactivedevicee.service.impl;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.entity.Device;
import dev.rlet.reactivedevicee.event.EventPublisher;
import dev.rlet.reactivedevicee.event.IpUpdatedEvent;
import dev.rlet.reactivedevicee.repository.device.DeviceRepository;
import dev.rlet.reactivedevicee.service.DeviceService;
import dev.rlet.reactivedevicee.util.DeviceMapper;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "DEVICE-SERVICE")
public class DeviceServiceImpl implements DeviceService {

  private final DeviceRepository deviceRepository;
  private final DeviceMapper deviceMapper;
  private final TransactionalOperator deviceTransactionalOperator;
  private final EventPublisher eventPublisher;

  @Override
  public Mono<DeviceResponse> createDevice(DeviceCreateRequest request) {
    return deviceRepository
        .save(deviceMapper.toCreateEntity(request))
        .map(deviceMapper::toResponse)
        .as(deviceTransactionalOperator::transactional);
  }

  @Override
  public Mono<DeviceResponse> updateDevice(Long id, DeviceUpdateRequest request) {
    return deviceRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Device not found")))
        .map(
            existingDevice -> {
              Device updatedDevice = deviceMapper.toUpdateEntity(id, request);
              return Tuples.of(existingDevice, updatedDevice);
            })
        .flatMap(
            tuple -> {
              Device existingDevice = tuple.getT1();
              Device updatedDevice = tuple.getT2();
              return deviceRepository
                  .save(updatedDevice)
                  .doOnNext(
                      savedDevice -> checkAndPublishIpChangeEvent(existingDevice, savedDevice));
            })
        .map(deviceMapper::toResponse)
        .as(deviceTransactionalOperator::transactional);
  }

  private void checkAndPublishIpChangeEvent(Device oldDevice, Device newDevice) {
    String oldIp = oldDevice.getIp();
    String newIp = newDevice.getIp();

    if (!Objects.equals(oldIp, newIp)) {
      IpUpdatedEvent event = new IpUpdatedEvent(newDevice.getImsi(), newIp);
      eventPublisher.publish(event);
      log.info("Event published!");
    }
  }

  @Override
  public Mono<DeviceResponse> getDevice(Long id) {
    return deviceRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoSuchElementException()))
        .map(deviceMapper::toResponse)
        .as(deviceTransactionalOperator::transactional);
  }

  @Override
  public Flux<DeviceResponse> getAllDevices(Integer page, Integer size) {
    int pageNumber = page != null ? page : 0;
    int pageSize = size != null ? size : 10;

    return deviceRepository
        .findDevices(pageNumber, pageSize)
        .map(deviceMapper::toResponse)
        .as(flux -> flux.as(deviceTransactionalOperator::transactional));
  }
}
