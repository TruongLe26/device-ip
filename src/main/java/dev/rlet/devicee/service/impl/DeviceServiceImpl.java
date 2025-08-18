package dev.rlet.devicee.service.impl;

import dev.rlet.devicee.dto.DeviceResponse;
import dev.rlet.devicee.dto.DeviceUpdateRequest;
import dev.rlet.devicee.entity.devices.Device;
import dev.rlet.devicee.event.IpUpdateSubject;
import dev.rlet.devicee.event.IpUpdatedEvent;
import dev.rlet.devicee.repository.devices.DeviceRepository;
import dev.rlet.devicee.service.DeviceService;
import dev.rlet.devicee.util.DeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "DEVICE-SERVICE")
public class DeviceServiceImpl implements DeviceService {

  private final DeviceRepository deviceRepository;
  private final DeviceMapper mapper;
  private final IpUpdateSubject ipUpdateSubject;

  @Override
  @Transactional("devicesTransactionManager")
  public DeviceResponse updateDevice(Long id, DeviceUpdateRequest request) {
    Device existingDevice =
        deviceRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + id));

    if (!existingDevice.getIp().equals(request.getIp())) {
      log.info("IP address changed for device with id: {}", id);
      ipUpdateSubject.notifyObservers(
          new IpUpdatedEvent(existingDevice.getImsi(), request.getIp()));
    }

    mapper.updateDeviceFromRequest(request, existingDevice);
    log.info("Updated device with id: {}", id);
    return mapper.toResponse(existingDevice);
  }
}
