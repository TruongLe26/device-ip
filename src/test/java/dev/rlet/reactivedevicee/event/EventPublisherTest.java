package dev.rlet.reactivedevicee.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

  @Mock private Sinks.Many<IpUpdatedEvent> ipUpdatedEventSink;
  @Mock private Flux<IpUpdatedEvent> eventFlux;
  @InjectMocks private EventPublisher eventPublisher;

  @Test
  void getIpUpdatedEvents_ShouldReturnFlux() {
    when(ipUpdatedEventSink.asFlux()).thenReturn(eventFlux);

    Flux<IpUpdatedEvent> result = eventPublisher.getIpUpdatedEvents();

    assertThat(result).isEqualTo(eventFlux);
    verify(ipUpdatedEventSink).asFlux();
  }

  @Test
  void emitIpUpdatedEvent_ShouldEmitEvent() {
    IpUpdatedEvent event = new IpUpdatedEvent("123456789", "192.168.1.1");
    when(ipUpdatedEventSink.tryEmitNext(event)).thenReturn(Sinks.EmitResult.OK);

    eventPublisher.publish(event);

    verify(ipUpdatedEventSink).tryEmitNext(event);
  }
}
