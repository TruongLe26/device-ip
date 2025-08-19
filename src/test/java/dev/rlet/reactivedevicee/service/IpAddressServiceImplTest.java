package dev.rlet.reactivedevicee.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import dev.rlet.reactivedevicee.entity.IpAddress;
import dev.rlet.reactivedevicee.repository.ip.IpAddressRepository;
import dev.rlet.reactivedevicee.service.impl.IpAddressServiceImpl;
import dev.rlet.reactivedevicee.util.IpAddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class IpAddressServiceImplTest {

  @Mock private IpAddressRepository ipAddressRepository;
  @Mock private IpAddressMapper ipAddressMapper;
  @Mock private TransactionalOperator ipTransactionalOperator;
  @InjectMocks private IpAddressServiceImpl ipAddressService;

  private IpAddress ipAddress;
  private IpAddressRequest ipAddressRequest;
  private IpAddressResponse ipAddressResponse;

  @BeforeEach
  void setUp() {
    ipAddress = new IpAddress();
    ipAddress.setImsi("123456789");
    ipAddress.setIp("192.168.1.1");

    ipAddressRequest = new IpAddressRequest();
    ipAddressResponse = new IpAddressResponse();
  }

  @Test
  void createIpAddress_Success() {
    when(ipAddressMapper.toEntity(ipAddressRequest)).thenReturn(ipAddress);
    when(ipAddressRepository.save(ipAddress)).thenReturn(Mono.just(ipAddress));
    when(ipAddressMapper.toResponse(ipAddress)).thenReturn(ipAddressResponse);
    when(ipTransactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Mono<IpAddressResponse> result = ipAddressService.createIpAddress(ipAddressRequest);

    StepVerifier.create(result).expectNext(ipAddressResponse).verifyComplete();

    verify(ipAddressRepository).save(ipAddress);
    verify(ipAddressMapper).toEntity(ipAddressRequest);
    verify(ipAddressMapper).toResponse(ipAddress);
  }

  @Test
  void listIpAddresses_Success() {
    IpAddress ipAddress2 = new IpAddress();
    ipAddress2.setImsi("987654321");
    ipAddress2.setIp("192.168.1.2");

    IpAddressResponse ipAddressResponse2 = new IpAddressResponse();

    when(ipAddressRepository.findAll()).thenReturn(Flux.just(ipAddress, ipAddress2));
    when(ipAddressMapper.toResponse(ipAddress)).thenReturn(ipAddressResponse);
    when(ipAddressMapper.toResponse(ipAddress2)).thenReturn(ipAddressResponse2);
    when(ipTransactionalOperator.transactional(any(Flux.class)))
        .thenAnswer(invocation -> (Flux<?>) invocation.getArgument(0));

    Flux<IpAddressResponse> result = ipAddressService.listIpAddresses();

    StepVerifier.create(result)
        .expectNext(ipAddressResponse)
        .expectNext(ipAddressResponse2)
        .verifyComplete();

    verify(ipAddressRepository).findAll();
    verify(ipAddressMapper).toResponse(ipAddress);
    verify(ipAddressMapper).toResponse(ipAddress2);
  }

  @Test
  void updateIpAddress_Success() {
    String imsi = "123456789";
    String newIp = "192.168.1.100";

    when(ipAddressRepository.findByImsi(imsi)).thenReturn(Mono.just(ipAddress));
    when(ipAddressRepository.updateIpAddress(any(IpAddress.class)))
        .thenReturn(Mono.just(ipAddress));

    Mono<Void> result = ipAddressService.updateIpAddress(imsi, newIp);

    StepVerifier.create(result).verifyComplete();

    verify(ipAddressRepository).findByImsi(imsi);
    verify(ipAddressRepository)
        .updateIpAddress(argThat(ip -> ip.getImsi().equals(imsi) && ip.getIp().equals(newIp)));
  }

  @Test
  void updateIpAddress_IpAddressNotFound() {
    String imsi = "123456789";
    String newIp = "192.168.1.100";

    when(ipAddressRepository.findByImsi(imsi)).thenReturn(Mono.empty());

    Mono<Void> result = ipAddressService.updateIpAddress(imsi, newIp);

    StepVerifier.create(result).verifyComplete();

    verify(ipAddressRepository).findByImsi(imsi);
    verify(ipAddressRepository, never()).updateIpAddress(any());
  }
}
