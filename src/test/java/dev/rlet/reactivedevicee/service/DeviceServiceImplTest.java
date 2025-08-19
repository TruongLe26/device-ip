package dev.rlet.reactivedevicee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.entity.Device;
import dev.rlet.reactivedevicee.event.EventPublisher;
import dev.rlet.reactivedevicee.event.IpUpdatedEvent;
import dev.rlet.reactivedevicee.repository.device.DeviceRepository;
import dev.rlet.reactivedevicee.service.impl.DeviceServiceImpl;
import dev.rlet.reactivedevicee.util.DeviceMapper;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTest {

  @Mock private DeviceRepository deviceRepository;
  @Mock private DeviceMapper deviceMapper;
  @Mock private TransactionalOperator deviceTransactionalOperator;
  @Mock private EventPublisher eventPublisher;
  @InjectMocks private DeviceServiceImpl deviceService;

  private Device device;
  private DeviceCreateRequest createRequest;
  private DeviceUpdateRequest updateRequest;
  private DeviceResponse deviceResponse;

  @BeforeEach
  void setUp() {
    device = new Device();
    device.setId(1L);
    device.setImsi("123456789");
    device.setIp("192.168.1.1");

    createRequest = new DeviceCreateRequest();
    updateRequest = new DeviceUpdateRequest();
    deviceResponse = new DeviceResponse();

    when(deviceTransactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void createDevice_Success() {
    when(deviceMapper.toCreateEntity(createRequest)).thenReturn(device);
    when(deviceRepository.save(device)).thenReturn(Mono.just(device));
    when(deviceMapper.toResponse(device)).thenReturn(deviceResponse);

    Mono<DeviceResponse> result = deviceService.createDevice(createRequest);

    StepVerifier.create(result).expectNext(deviceResponse).verifyComplete();

    verify(deviceRepository).save(device);
    verify(deviceMapper).toCreateEntity(createRequest);
    verify(deviceMapper).toResponse(device);
  }

  @Test
  void updateDevice_Success_WithoutIpChange() {
    Device updatedDevice = new Device();
    updatedDevice.setId(1L);
    updatedDevice.setImsi("123456789");
    updatedDevice.setIp("192.168.1.1"); // Same IP

    when(deviceRepository.findById(1L)).thenReturn(Mono.just(device));
    when(deviceMapper.toUpdateEntity(1L, updateRequest)).thenReturn(updatedDevice);
    when(deviceRepository.save(updatedDevice)).thenReturn(Mono.just(updatedDevice));
    when(deviceMapper.toResponse(updatedDevice)).thenReturn(deviceResponse);

    Mono<DeviceResponse> result = deviceService.updateDevice(1L, updateRequest);

    StepVerifier.create(result).expectNext(deviceResponse).verifyComplete();

    verify(deviceRepository).findById(1L);
    verify(deviceRepository).save(updatedDevice);
    verify(eventPublisher, never()).publish(any());
  }

  @Test
  void updateDevice_Success_WithIpChange() {
    Device updatedDevice = new Device();
    updatedDevice.setId(1L);
    updatedDevice.setImsi("123456789");
    updatedDevice.setIp("192.168.1.2"); // Different IP

    when(deviceRepository.findById(1L)).thenReturn(Mono.just(device));
    when(deviceMapper.toUpdateEntity(1L, updateRequest)).thenReturn(updatedDevice);
    when(deviceRepository.save(updatedDevice)).thenReturn(Mono.just(updatedDevice));
    when(deviceMapper.toResponse(updatedDevice)).thenReturn(deviceResponse);

    Mono<DeviceResponse> result = deviceService.updateDevice(1L, updateRequest);

    StepVerifier.create(result).expectNext(deviceResponse).verifyComplete();

    ArgumentCaptor<IpUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(IpUpdatedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    IpUpdatedEvent capturedEvent = eventCaptor.getValue();
    assertEquals("123456789", capturedEvent.imsi());
    assertEquals("192.168.1.2", capturedEvent.newIp());
  }

  @Test
  void updateDevice_DeviceNotFound() {
    when(deviceRepository.findById(1L)).thenReturn(Mono.empty());

    Mono<DeviceResponse> result = deviceService.updateDevice(1L, updateRequest);

    StepVerifier.create(result).expectError(IllegalArgumentException.class).verify();

    verify(deviceRepository, never()).save(any());
    verify(eventPublisher, never()).publish(any());
  }

  @Test
  void getDevice_Success() {
    when(deviceRepository.findById(1L)).thenReturn(Mono.just(device));
    when(deviceMapper.toResponse(device)).thenReturn(deviceResponse);

    Mono<DeviceResponse> result = deviceService.getDevice(1L);

    StepVerifier.create(result).expectNext(deviceResponse).verifyComplete();

    verify(deviceRepository).findById(1L);
    verify(deviceMapper).toResponse(device);
  }

  @Test
  void getDevice_DeviceNotFound() {
    when(deviceRepository.findById(1L)).thenReturn(Mono.empty());

    Mono<DeviceResponse> result = deviceService.getDevice(1L);

    StepVerifier.create(result).expectError(NoSuchElementException.class).verify();

    verify(deviceMapper, never()).toResponse(any());
  }
}
