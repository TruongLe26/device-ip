package dev.rlet.reactivedevicee.controller;

import static org.mockito.Mockito.*;

import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import dev.rlet.reactivedevicee.service.IpAddressService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = IpController.class)
public class IpControllerTest {

  @Autowired private WebTestClient webTestClient;
  @MockitoBean private IpAddressService ipAddressService;

  @Test
  void shouldCreateIpAddressSuccessfully() {
    IpAddressRequest request =
        IpAddressRequest.builder().imsi("123456789012345").ip("192.168.1.100").build();

    IpAddressResponse response =
        IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Mono.just(response)).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(IpAddressResponse.class)
        .isEqualTo(response);

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldReturnBadRequestWhenCreateIpAddressFails() {
    IpAddressRequest request =
        IpAddressRequest.builder().imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Mono.empty()).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldListIpAddressesSuccessfully() {
    List<IpAddressResponse> responses =
        Arrays.asList(
            IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("192.168.1.100").build(),
            IpAddressResponse.builder().id(2L).imsi("123456789012346").ip("192.168.1.101").build());

    doReturn(Flux.fromIterable(responses)).when(ipAddressService).listIpAddresses();

    webTestClient
        .get()
        .uri("/ip-addresses")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(IpAddressResponse.class)
        .hasSize(2)
        .contains(responses.toArray(new IpAddressResponse[0]));

    verify(ipAddressService).listIpAddresses();
  }

  @Test
  void shouldListEmptyIpAddresses() {
    doReturn(Flux.empty()).when(ipAddressService).listIpAddresses();

    webTestClient
        .get()
        .uri("/ip-addresses")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(IpAddressResponse.class)
        .hasSize(0);

    verify(ipAddressService).listIpAddresses();
  }

  @Test
  void shouldHandleInvalidRequestBody() {
    webTestClient
        .post()
        .uri("/ip-addresses")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{invalid json}")
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(ipAddressService);
  }

  @Test
  void shouldHandleEmptyRequestBody() {
    webTestClient
        .post()
        .uri("/ip-addresses")
        .contentType(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest();

    verifyNoInteractions(ipAddressService);
  }

  @Test
  void shouldHandleCreateServiceException() {
    IpAddressRequest request =
        IpAddressRequest.builder().imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Mono.error(new RuntimeException("Database error")))
        .when(ipAddressService)
        .createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is5xxServerError();

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldHandleListServiceException() {
    doReturn(Flux.error(new RuntimeException("Database connection failed")))
        .when(ipAddressService)
        .listIpAddresses();

    webTestClient.get().uri("/ip-addresses").exchange().expectStatus().is5xxServerError();

    verify(ipAddressService).listIpAddresses();
  }

  @Test
  void shouldVerifyCorrectContentType() {
    IpAddressRequest request =
        IpAddressRequest.builder().imsi("123456789012345").ip("192.168.1.100").build();

    IpAddressResponse response =
        IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Mono.just(response)).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody(IpAddressResponse.class)
        .isEqualTo(response);
  }

  @Test
  void shouldHandleNullImsiInRequest() {
    IpAddressRequest request =
        IpAddressRequest.builder()
            .ip("192.168.1.100")
            // imsi is null
            .build();

    IpAddressResponse response = IpAddressResponse.builder().id(1L).ip("192.168.1.100").build();

    doReturn(Mono.just(response)).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(IpAddressResponse.class)
        .isEqualTo(response);

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldHandleNullIpInRequest() {
    IpAddressRequest request =
        IpAddressRequest.builder()
            .imsi("123456789012345")
            // ip is null
            .build();

    IpAddressResponse response = IpAddressResponse.builder().id(1L).imsi("123456789012345").build();

    doReturn(Mono.just(response)).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(IpAddressResponse.class)
        .isEqualTo(response);

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldHandleCreateWithValidationError() {
    IpAddressRequest request =
        IpAddressRequest.builder().imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Mono.error(new IllegalArgumentException("Invalid IP format")))
        .when(ipAddressService)
        .createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is5xxServerError();

    verify(ipAddressService).createIpAddress(request);
  }

  @Test
  void shouldListIpAddressesWithSingleItem() {
    IpAddressResponse response =
        IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("192.168.1.100").build();

    doReturn(Flux.just(response)).when(ipAddressService).listIpAddresses();

    webTestClient
        .get()
        .uri("/ip-addresses")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(IpAddressResponse.class)
        .hasSize(1)
        .contains(response);

    verify(ipAddressService).listIpAddresses();
  }

  @Test
  void shouldHandleListIpAddressesStreamingResponse() {
    List<IpAddressResponse> responses =
        Arrays.asList(
            IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("192.168.1.100").build(),
            IpAddressResponse.builder().id(2L).imsi("123456789012346").ip("192.168.1.101").build(),
            IpAddressResponse.builder().id(3L).imsi("123456789012347").ip("192.168.1.102").build());

    doReturn(Flux.fromIterable(responses)).when(ipAddressService).listIpAddresses();

    webTestClient
        .get()
        .uri("/ip-addresses")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[0].id")
        .isEqualTo(1)
        .jsonPath("$[0].imsi")
        .isEqualTo("123456789012345")
        .jsonPath("$[0].ip")
        .isEqualTo("192.168.1.100")
        .jsonPath("$[1].id")
        .isEqualTo(2)
        .jsonPath("$[2].id")
        .isEqualTo(3);

    verify(ipAddressService).listIpAddresses();
  }

  @Test
  void shouldHandleCreateIpAddressWithSpecialCharacters() {
    IpAddressRequest request =
        IpAddressRequest.builder()
            .imsi("123456789012345")
            .ip("::1") // IPv6 loopback
            .build();

    IpAddressResponse response =
        IpAddressResponse.builder().id(1L).imsi("123456789012345").ip("::1").build();

    doReturn(Mono.just(response)).when(ipAddressService).createIpAddress(request);

    webTestClient
        .post()
        .uri("/ip-addresses")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(IpAddressResponse.class)
        .isEqualTo(response);

    verify(ipAddressService).createIpAddress(request);
  }
}
