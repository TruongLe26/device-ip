package dev.rlet.reactivedevicee.event;

import dev.rlet.reactivedevicee.service.IpAddressService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EVENT-SUBSCRIBER")
public class EventSubscriber {

  private final EventPublisher eventPublisher;
  private final IpAddressService ipAddressService;

  @PostConstruct
  public void init() {
    eventPublisher.getIpUpdatedEvents().flatMap(this::handleIpUpdatedEvent).subscribe();
  }

  private Mono<Void> handleIpUpdatedEvent(IpUpdatedEvent event) {
    log.info("Received IP updated event - IMSI: {}, New IP: {}", event.imsi(), event.newIp());
    return ipAddressService.updateIpAddress(event.imsi(), event.newIp());
  }
}
