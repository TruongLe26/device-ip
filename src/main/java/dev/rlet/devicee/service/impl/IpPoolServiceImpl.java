package dev.rlet.devicee.service.impl;

import dev.rlet.devicee.repository.ips.IpPoolRepository;
import dev.rlet.devicee.service.IpPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "IP-POOL-SERVICE")
public class IpPoolServiceImpl implements IpPoolService {

  private final IpPoolRepository ipPoolRepository;

  @Override
  @Transactional("ipsTransactionManager")
  public void updateIpAddress(String imsi, String newIp) {
    ipPoolRepository.findByImsi(imsi).ifPresent(ipAddress -> ipAddress.setIp(newIp));
    log.info("IP updated for IMSI: {}", imsi);
  }
}
