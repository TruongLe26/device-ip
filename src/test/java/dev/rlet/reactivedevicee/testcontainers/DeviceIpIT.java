package dev.rlet.reactivedevicee.testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.event.EventPublisher;
import dev.rlet.reactivedevicee.event.IpUpdatedEvent;
import dev.rlet.reactivedevicee.repository.device.DeviceRepository;
import dev.rlet.reactivedevicee.repository.ip.IpAddressRepository;
import dev.rlet.reactivedevicee.service.DeviceService;
import dev.rlet.reactivedevicee.service.IpAddressService;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Testcontainers
@DirtiesContext
public class DeviceIpIT extends BaseTestcontainersTest {

  @Autowired private DeviceService deviceService;
  @Autowired private IpAddressService ipAddressService;
  @Autowired private DeviceRepository deviceRepository;
  @Autowired private IpAddressRepository ipAddressRepository;
  @Autowired private EventPublisher eventPublisher;

  @BeforeEach
  void setUp() {
    deviceRepository.deleteAll().block();
    ipAddressRepository.deleteAll().block();
  }

  @Test
  void deviceIpUpdate_ShouldTriggerIpAddressUpdate() {
    String model = "Tesla X";
    String imsi = "123456789";
    String initialIp = "192.168.1.1";
    String newIp = "192.168.1.100";

    DeviceCreateRequest createRequest =
        DeviceCreateRequest.builder().imsi(imsi).ip(initialIp).model(model).build();

    IpAddressRequest ipRequest = IpAddressRequest.builder().imsi(imsi).ip(initialIp).build();

    DeviceResponse createdDevice = deviceService.createDevice(createRequest).block();
    ipAddressService.createIpAddress(ipRequest).block();

    assertThat(createdDevice).isNotNull();
    assertThat(createdDevice.getIp()).isEqualTo(initialIp);

    DeviceUpdateRequest updateRequest = DeviceUpdateRequest.builder().imsi(imsi).ip(newIp).build();

    Mono<IpUpdatedEvent> eventMono =
        eventPublisher
            .getIpUpdatedEvents()
            .filter(event -> imsi.equals(event.imsi()))
            .next()
            .timeout(Duration.ofSeconds(5));
    Mono<DeviceResponse> updateMono =
        deviceService.updateDevice(createdDevice.getId(), updateRequest);

    StepVerifier.create(updateMono.then(eventMono))
        .expectNextMatches(event -> imsi.equals(event.imsi()) && newIp.equals(event.newIp()))
        .verifyComplete();

    StepVerifier.create(ipAddressRepository.findByImsi(imsi))
        .expectNextMatches(ipAddress -> ipAddress.getIp().equals(newIp))
        .verifyComplete();
  }
}
