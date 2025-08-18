package dev.rlet.devicee.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ObserverConfiguration {

  private final IpPoolObserver ipPoolObserver;

  @Bean
  public IpUpdateSubject ipUpdateSubject() {
    IpUpdateSubject subject = new IpUpdateSubject(true);
    subject.attach(ipPoolObserver);
    return subject;
  }
}
