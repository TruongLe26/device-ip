package dev.rlet.devicee.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Primary
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "dev.rlet.devicee.repository.devices",
    entityManagerFactoryRef = "devicesEntityManagerFactory",
    transactionManagerRef = "devicesTransactionManager")
public class DevicesDataSourceConfiguration {

  @Primary
  @Bean(name = "devicesDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.devices")
  public DataSource devicesDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean(name = "devicesEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean devicesEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("devicesDataSource") DataSource dataSource) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    properties.put("hibernate.hbm2ddl.auto", "create-drop");
    properties.put("hibernate.show_sql", "true");

    return builder
        .dataSource(dataSource)
        .packages("dev.rlet.devicee.entity.devices")
        .persistenceUnit("devices")
        .properties(properties)
        .build();
  }

  @Primary
  @Bean(name = "devicesTransactionManager")
  public PlatformTransactionManager devicesTransactionManager(
      @Qualifier("devicesEntityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean
  public DataSourceInitializer devicesDataSourceInitializer(
      @Qualifier("devicesDataSource") DataSource dataSource) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("devices-data.sql"));

    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(populator);
    return initializer;
  }
}
