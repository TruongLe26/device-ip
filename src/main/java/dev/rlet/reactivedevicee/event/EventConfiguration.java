package dev.rlet.reactivedevicee.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class EventConfiguration {

  @Bean
  public Sinks.Many<IpUpdatedEvent> ipUpdatedEventSink() {
    return Sinks.many().replay().latest();
  }
}
