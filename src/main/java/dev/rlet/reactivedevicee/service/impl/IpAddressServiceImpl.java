package dev.rlet.reactivedevicee.service.impl;

import dev.rlet.reactivedevicee.dto.IpAddressRequest;
import dev.rlet.reactivedevicee.dto.IpAddressResponse;
import dev.rlet.reactivedevicee.repository.ip.IpAddressRepository;
import dev.rlet.reactivedevicee.service.IpAddressService;
import dev.rlet.reactivedevicee.util.IpAddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "IP-ADDRESS-SERVICE")
public class IpAddressServiceImpl implements IpAddressService {

  private final IpAddressRepository ipAddressRepository;
  private final IpAddressMapper ipAddressMapper;
  private final TransactionalOperator ipTransactionalOperator;

  @Override
  public Mono<IpAddressResponse> createIpAddress(IpAddressRequest request) {
    return ipAddressRepository
        .save(ipAddressMapper.toEntity(request))
        .map(ipAddressMapper::toResponse)
        .as(ipTransactionalOperator::transactional);
  }

  @Override
  public Flux<IpAddressResponse> listIpAddresses() {
    return ipAddressRepository
        .findAll()
        .map(ipAddressMapper::toResponse)
        .as(ipTransactionalOperator::transactional);
  }

  @Override
  public Mono<Void> updateIpAddress(String imsi, String newIp) {
    return ipAddressRepository
        .findByImsi(imsi)
        .flatMap(
            ipAddress -> {
              ipAddress.setIp(newIp);
              return ipAddressRepository.updateIpAddress(ipAddress);
            })
        .doOnSuccess(
            updatedIp -> log.info("IP address updated for IMSI: {}, new IP: {}", imsi, newIp))
        .then()
        .doOnError(
            error ->
                log.error(
                    "Failed to update IP address for IMSI: {}, error: {}",
                    imsi,
                    error.getMessage()));
  }
}
