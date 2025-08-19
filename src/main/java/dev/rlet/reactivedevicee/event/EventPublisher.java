package dev.rlet.reactivedevicee.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EVENT-PUBLISHER")
public class EventPublisher {

  private final Sinks.Many<IpUpdatedEvent> ipUpdatedEventSink;

  public Flux<IpUpdatedEvent> getIpUpdatedEvents() {
    return ipUpdatedEventSink.asFlux();
  }

  public void publish(IpUpdatedEvent event) {
    ipUpdatedEventSink.tryEmitNext(event);
    log.info("Published IP updated event: imsi={}, newIp={}", event.imsi(), event.newIp());
  }
}
