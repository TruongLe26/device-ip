package dev.rlet.reactivedevicee.repository.device;

import dev.rlet.reactivedevicee.entity.Device;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class CustomDeviceRepositoryImpl implements CustomDeviceRepository {

  private final R2dbcEntityOperations r2dbcEntityOperations;

  public CustomDeviceRepositoryImpl(
      @Qualifier("deviceR2dbcEntityOperations") R2dbcEntityOperations r2dbcEntityOperations) {
    this.r2dbcEntityOperations = r2dbcEntityOperations;
  }

  @Override
  public Flux<Device> findDevices(int pageNumber, int pageSize) {
    if (pageNumber == 0) {
      return findFirstPage(pageSize);
    }
    return findSubsequentPage(pageNumber, pageSize);
  }

  private Flux<Device> findFirstPage(int pageSize) {
    Query query = Query.query(Criteria.empty()).sort(Sort.by("id").ascending()).limit(pageSize);

    return r2dbcEntityOperations.select(Device.class).matching(query).all();
  }

  private Flux<Device> findSubsequentPage(int pageNumber, int pageSize) {
    int recordsToSkip = pageNumber * pageSize;

    Query boundaryQuery =
        Query.query(Criteria.empty())
            .sort(Sort.by("id").ascending())
            .limit(pageSize)
            .offset(recordsToSkip - 1);

    return r2dbcEntityOperations
        .select(Device.class)
        .matching(boundaryQuery)
        .first()
        .flatMapMany(
            boundaryDevice -> {
              Query query =
                  Query.query(Criteria.where("id").greaterThan(boundaryDevice.getId()))
                      .sort(Sort.by("id").ascending())
                      .limit(pageSize);

              return r2dbcEntityOperations.select(Device.class).matching(query).all();
            })
        .switchIfEmpty(Flux.empty());
  }
}
