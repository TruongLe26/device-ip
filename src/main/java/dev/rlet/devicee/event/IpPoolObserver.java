package dev.rlet.devicee.event;

import dev.rlet.devicee.service.IpPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "IP-POOL-OBSERVER")
public class IpPoolObserver implements Observer<IpUpdatedEvent> {

  private final IpPoolService ipPoolService;

  @Override
  public void update(IpUpdatedEvent event) {
    String imsi = event.imsi();
    String newIp = event.newIp();

    log.info("Received IP update event, processing...");
    ipPoolService.updateIpAddress(imsi, newIp);
    log.info("IP address updated for IMSI: {}, new IP: {}", imsi, newIp);
  }
}
