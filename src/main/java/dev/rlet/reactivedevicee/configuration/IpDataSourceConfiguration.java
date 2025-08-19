package dev.rlet.reactivedevicee.configuration;

import dev.rlet.reactivedevicee.configuration.properties.IpDataSourceProperties;
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
import org.springframework.data.r2dbc.dialect.H2Dialect;
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
    basePackages = "dev.rlet.reactivedevicee.repository.ip",
    entityOperationsRef = "ipR2dbcEntityOperations")
public class IpDataSourceConfiguration {

  private final IpDataSourceProperties properties;

  @Bean(name = "ipConnectionFactory")
  @NonNull
  public ConnectionFactory connectionFactory() {
    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "h2")
            .option(ConnectionFactoryOptions.PROTOCOL, properties.getProtocol())
            .option(ConnectionFactoryOptions.DATABASE, properties.getDatabase())
            .option(ConnectionFactoryOptions.USER, properties.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, properties.getPassword())
            .build();

    ConnectionFactory connectionFactory = ConnectionFactories.get(options);

    return new ConnectionPool(
        ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMinutes(10))
            .initialSize(1)
            .maxSize(10)
            .build());
  }

  @Bean
  public ConnectionFactoryInitializer ipInitializer(
      @Qualifier("ipConnectionFactory") ConnectionFactory connectionFactory) {
    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("h2/schema.sql")));
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("h2/data.sql")));
    initializer.setDatabasePopulator(populator);
    return initializer;
  }

  @Bean(name = "ipDatabaseClient")
  public DatabaseClient databaseClient(
      @Qualifier("ipConnectionFactory") ConnectionFactory connectionFactory) {
    return DatabaseClient.create(connectionFactory);
  }

  @Bean(name = "ipR2dbcEntityOperations")
  public R2dbcEntityOperations r2dbcEntityOperations(
      @Qualifier("ipDatabaseClient") DatabaseClient databaseClient) {
    DefaultReactiveDataAccessStrategy strategy =
        new DefaultReactiveDataAccessStrategy(H2Dialect.INSTANCE);
    return new R2dbcEntityTemplate(databaseClient, strategy);
  }

  @Bean(name = "ipTransactionManager")
  public ReactiveTransactionManager transactionManager(
      @Qualifier("ipConnectionFactory") ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  @Bean(name = "ipTransactionalOperator")
  public TransactionalOperator transactionalOperator(
      @Qualifier("ipTransactionManager") ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager);
  }
}
