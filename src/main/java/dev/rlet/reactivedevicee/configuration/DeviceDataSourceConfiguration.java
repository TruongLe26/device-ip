package dev.rlet.reactivedevicee.configuration;

import dev.rlet.reactivedevicee.configuration.properties.DeviceDataSourceProperties;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@RequiredArgsConstructor
@EnableR2dbcRepositories(
    basePackages = "dev.rlet.reactivedevicee.repository.device",
    entityOperationsRef = "deviceR2dbcEntityOperations")
public class DeviceDataSourceConfiguration {

  private final DeviceDataSourceProperties properties;

  @Bean(name = "deviceConnectionFactory")
  @NonNull
  public ConnectionFactory connectionFactory() {
    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "mysql")
            .option(ConnectionFactoryOptions.HOST, properties.getHost())
            .option(ConnectionFactoryOptions.PORT, properties.getPort())
            .option(ConnectionFactoryOptions.DATABASE, properties.getDatabase())
            .option(ConnectionFactoryOptions.USER, properties.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, properties.getPassword())
            .build();

    ConnectionFactory connectionFactory = ConnectionFactories.get(options);

    return new ConnectionPool(
        ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMinutes(20))
            .initialSize(5)
            .maxSize(20)
            .build());
  }

  @Bean
  public ConnectionFactoryInitializer deviceInitializer(
      @Qualifier("deviceConnectionFactory") ConnectionFactory connectionFactory) {
    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator.addPopulators(
        new ResourceDatabasePopulator(new ClassPathResource("mysql/schema.sql")));
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("mysql/data.sql")));
    initializer.setDatabasePopulator(populator);
    return initializer;
  }

  // In case we want to use this directly
  @Bean(name = "deviceDatabaseClient")
  public DatabaseClient databaseClient(
      @Qualifier("deviceConnectionFactory") ConnectionFactory connectionFactory) {
    return DatabaseClient.create(connectionFactory);
  }

  // Config for R2dbcRepository
  @Bean(name = "deviceR2dbcEntityOperations")
  public R2dbcEntityOperations r2dbcEntityOperations(
      @Qualifier("deviceDatabaseClient") DatabaseClient databaseClient) {
    DefaultReactiveDataAccessStrategy strategy =
        new DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
    return new R2dbcEntityTemplate(databaseClient, strategy);
  }

  @Bean(name = "deviceTransactionManager")
  public ReactiveTransactionManager transactionManager(
      @Qualifier("deviceConnectionFactory") ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  // Config for transaction support
  @Bean(name = "deviceTransactionalOperator")
  public TransactionalOperator transactionalOperator(
      @Qualifier("deviceTransactionManager") ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager);
  }
}
