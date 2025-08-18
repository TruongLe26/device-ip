package dev.rlet.devicee.repository.devices;

import dev.rlet.devicee.entity.devices.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {}
