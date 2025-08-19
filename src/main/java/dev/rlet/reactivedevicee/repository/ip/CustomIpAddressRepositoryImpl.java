package dev.rlet.reactivedevicee.repository.ip;

import dev.rlet.reactivedevicee.entity.IpAddress;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CustomIpAddressRepositoryImpl implements CustomIpAddressRepository {

  private final DatabaseClient ipDatabaseClient;

  public CustomIpAddressRepositoryImpl(
      @Qualifier("ipDatabaseClient") DatabaseClient ipDatabaseClient) {
    this.ipDatabaseClient = ipDatabaseClient;
  }

  @Override
  public Mono<IpAddress> updateIpAddress(IpAddress ipAddress) {
    return ipDatabaseClient
        .sql("UPDATE ip_address SET ip = ? WHERE imsi = ?")
        .bind(0, ipAddress.getIp())
        .bind(1, ipAddress.getImsi())
        .fetch()
        .rowsUpdated()
        .flatMap(
            count -> {
              if (count > 0) {
                return Mono.just(ipAddress);
              } else {
                return Mono.empty();
              }
            });
  }
}
