package dev.rlet.reactivedevicee.controller;

import static org.mockito.Mockito.*;

import dev.rlet.reactivedevicee.dto.DeviceCreateRequest;
import dev.rlet.reactivedevicee.dto.DeviceResponse;
import dev.rlet.reactivedevicee.dto.DeviceUpdateRequest;
import dev.rlet.reactivedevicee.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DeviceController.class)
public class DeviceControllerTest {

  @Autowired private WebTestClient webTestClient;
  @MockitoBean private DeviceService deviceService;

  @Test
  void shouldCreateDeviceSuccessfully() {
    DeviceCreateRequest request =
        DeviceCreateRequest.builder()
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    DeviceResponse response =
        DeviceResponse.builder()
            .id(1L)
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    doReturn(Mono.just(response)).when(deviceService).createDevice(request);

    webTestClient
        .post()
        .uri("/devices")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(DeviceResponse.class)
        .isEqualTo(response);

    verify(deviceService).createDevice(request);
  }

  @Test
  void shouldReturnBadRequestWhenCreateDeviceFails() {
    DeviceCreateRequest request =
        DeviceCreateRequest.builder()
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    doReturn(Mono.empty()).when(deviceService).createDevice(request);

    webTestClient
        .post()
        .uri("/devices")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verify(deviceService).createDevice(request);
  }

  @Test
  void shouldGetDevice() {
    Long deviceId = 1L;

    DeviceResponse response =
        DeviceResponse.builder()
            .id(deviceId)
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    doReturn(Mono.just(response)).when(deviceService).getDevice(deviceId);

    webTestClient
        .get()
        .uri("/devices/{id}", deviceId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(DeviceResponse.class)
        .isEqualTo(response);

    verify(deviceService).getDevice(deviceId);
  }

  @Test
  void shouldReturnNotFoundWhenDeviceDoesNotExist() {
    Long deviceId = 999L;

    doReturn(Mono.empty()).when(deviceService).getDevice(deviceId);

    webTestClient.get().uri("/devices/{id}", deviceId).exchange().expectStatus().isNotFound();

    verify(deviceService).getDevice(deviceId);
  }

  @Test
  void shouldUpdateDeviceSuccessfully() {
    Long deviceId = 1L;
    DeviceUpdateRequest updateRequest =
        DeviceUpdateRequest.builder().ip("192.168.1.200").model("Tesla Model S").build();

    DeviceResponse updatedResponse =
        DeviceResponse.builder()
            .id(deviceId)
            .imsi("123456789012345")
            .ip("192.168.1.200")
            .model("Tesla Model S")
            .build();

    doReturn(Mono.just(updatedResponse)).when(deviceService).updateDevice(deviceId, updateRequest);

    webTestClient
        .put()
        .uri("/devices/{id}", deviceId)
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(DeviceResponse.class)
        .isEqualTo(updatedResponse);

    verify(deviceService).updateDevice(deviceId, updateRequest);
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistentDevice() {
    Long deviceId = 999L;
    DeviceUpdateRequest updateRequest =
        DeviceUpdateRequest.builder().ip("192.168.1.200").model("Tesla Model S").build();

    doReturn(Mono.empty()).when(deviceService).updateDevice(deviceId, updateRequest);

    webTestClient
        .put()
        .uri("/devices/{id}", deviceId)
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isNotFound();

    verify(deviceService).updateDevice(deviceId, updateRequest);
  }

  @Test
  void shouldGetAllDevicesWithDefaultPagination() {
    DeviceResponse device1 =
        DeviceResponse.builder()
            .id(1L)
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    DeviceResponse device2 =
        DeviceResponse.builder()
            .id(2L)
            .imsi("123456789012346")
            .ip("192.168.1.101")
            .model("BMW")
            .build();

    Flux<DeviceResponse> deviceFlux = Flux.just(device1, device2);
    doReturn(deviceFlux).when(deviceService).getAllDevices(0, 20);

    webTestClient
        .get()
        .uri("/devices")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(DeviceResponse.class)
        .hasSize(2)
        .contains(device1, device2);

    verify(deviceService).getAllDevices(0, 20);
  }

  @Test
  void shouldGetAllDevicesWithCustomPagination() {
    Integer page = 2;
    Integer size = 5;

    DeviceResponse device1 =
        DeviceResponse.builder()
            .id(11L)
            .imsi("123456789012355")
            .ip("192.168.1.110")
            .model("Audi")
            .build();

    Flux<DeviceResponse> deviceFlux = Flux.just(device1);
    doReturn(deviceFlux).when(deviceService).getAllDevices(page, size);

    webTestClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/devices")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(DeviceResponse.class)
        .hasSize(1)
        .contains(device1);

    verify(deviceService).getAllDevices(page, size);
  }

  @Test
  void shouldGetAllDevicesWithOnlyPageParam() {
    Integer page = 1;

    DeviceResponse device1 =
        DeviceResponse.builder()
            .id(11L)
            .imsi("123456789012355")
            .ip("192.168.1.110")
            .model("Audi")
            .build();

    Flux<DeviceResponse> deviceFlux = Flux.just(device1);
    doReturn(deviceFlux).when(deviceService).getAllDevices(page, 20);

    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/devices").queryParam("page", page).build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(DeviceResponse.class)
        .hasSize(1)
        .contains(device1);

    verify(deviceService).getAllDevices(page, 20);
  }

  @Test
  void shouldGetAllDevicesWithOnlySizeParam() {
    Integer size = 15;

    DeviceResponse device1 =
        DeviceResponse.builder()
            .id(1L)
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    Flux<DeviceResponse> deviceFlux = Flux.just(device1);
    doReturn(deviceFlux).when(deviceService).getAllDevices(0, size);

    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/devices").queryParam("size", size).build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(DeviceResponse.class)
        .hasSize(1)
        .contains(device1);

    verify(deviceService).getAllDevices(0, size);
  }

  @Test
  void shouldReturnEmptyListWhenNoDevicesFound() {
    Flux<DeviceResponse> emptyFlux = Flux.empty();
    doReturn(emptyFlux).when(deviceService).getAllDevices(0, 20);

    webTestClient
        .get()
        .uri("/devices")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(DeviceResponse.class)
        .hasSize(0);

    verify(deviceService).getAllDevices(0, 20);
  }

  @Test
  void shouldHandleInvalidRequestBody() {
    webTestClient
        .post()
        .uri("/devices")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{invalid json}")
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(deviceService);
  }

  @Test
  void shouldHandleEmptyRequestBody() {
    webTestClient
        .post()
        .uri("/devices")
        .contentType(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(deviceService);
  }

  @Test
  void shouldHandleServiceException() {
    DeviceCreateRequest request =
        DeviceCreateRequest.builder()
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    doReturn(Mono.error(new RuntimeException("Database error")))
        .when(deviceService)
        .createDevice(request);

    webTestClient
        .post()
        .uri("/devices")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is5xxServerError();

    verify(deviceService).createDevice(request);
  }

  @Test
  void shouldHandleGetDeviceServiceException() {
    Long deviceId = 1L;

    doReturn(Mono.error(new RuntimeException("Database connection failed")))
        .when(deviceService)
        .getDevice(deviceId);

    webTestClient.get().uri("/devices/{id}", deviceId).exchange().expectStatus().is5xxServerError();

    verify(deviceService).getDevice(deviceId);
  }

  @Test
  void shouldHandleUpdateDeviceServiceException() {
    Long deviceId = 1L;
    DeviceUpdateRequest updateRequest = DeviceUpdateRequest.builder().ip("192.168.1.200").build();

    doReturn(Mono.error(new IllegalArgumentException("Invalid device data")))
        .when(deviceService)
        .updateDevice(deviceId, updateRequest);

    webTestClient
        .put()
        .uri("/devices/{id}", deviceId)
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .is5xxServerError();

    verify(deviceService).updateDevice(deviceId, updateRequest);
  }

  @Test
  void shouldHandleInvalidDeviceId() {
    webTestClient.get().uri("/devices/{id}", "invalid-id").exchange().expectStatus().isBadRequest();

    verifyNoInteractions(deviceService);
  }

  @Test
  void shouldVerifyCorrectContentType() {
    DeviceCreateRequest request =
        DeviceCreateRequest.builder()
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    DeviceResponse response =
        DeviceResponse.builder()
            .id(1L)
            .imsi("123456789012345")
            .ip("192.168.1.100")
            .model("Tesla")
            .build();

    doReturn(Mono.just(response)).when(deviceService).createDevice(request);

    webTestClient
        .post()
        .uri("/devices")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(DeviceResponse.class)
        .isEqualTo(response);
  }
}
