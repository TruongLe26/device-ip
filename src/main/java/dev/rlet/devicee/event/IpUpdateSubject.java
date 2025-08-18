package dev.rlet.devicee.event;

import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "IP-UPDATE-SUBJECT")
public class IpUpdateSubject implements Subject<IpUpdatedEvent> {

  private final List<Observer<IpUpdatedEvent>> observers = new CopyOnWriteArrayList<>();
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final boolean async;

  public IpUpdateSubject(boolean async) {
    this.async = async;
  }

  @Override
  public void attach(Observer<IpUpdatedEvent> observer) {
    observers.add(observer);
  }

  @Override
  public void detach(Observer<IpUpdatedEvent> observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(IpUpdatedEvent data) {
    for (Observer<IpUpdatedEvent> observer : observers) {
      if (async) {
        executor.submit(
            () -> {
              try {
                observer.update(data);
              } catch (Exception e) {
                log.error("Observer error: " + e.getMessage());
              }
            });
      } else {
        try {
          observer.update(data);
        } catch (Exception e) {
          log.error("Observer error: " + e.getMessage());
        }
      }
    }
  }

  @PreDestroy
  public void shutdown() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
