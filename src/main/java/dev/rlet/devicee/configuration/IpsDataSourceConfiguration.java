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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "dev.rlet.devicee.repository.ips",
    entityManagerFactoryRef = "ipsEntityManagerFactory",
    transactionManagerRef = "ipsTransactionManager")
public class IpsDataSourceConfiguration {

  @Bean(name = "ipsDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.ips")
  public DataSource ipsDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "ipsEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean ipsEntityManagerFactory(
      EntityManagerFactoryBuilder builder, @Qualifier("ipsDataSource") DataSource dataSource) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.put("hibernate.hbm2ddl.auto", "create-drop");
    properties.put("hibernate.show_sql", "true");

    return builder
        .dataSource(dataSource)
        .packages("dev.rlet.devicee.entity.ips")
        .persistenceUnit("ips")
        .properties(properties)
        .build();
  }

  @Bean(name = "ipsTransactionManager")
  public PlatformTransactionManager ipsTransactionManager(
      @Qualifier("ipsEntityManagerFactory") EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean
  public DataSourceInitializer ipsDataSourceInitializer(
      @Qualifier("ipsDataSource") DataSource dataSource) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("ips-data.sql"));

    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(populator);
    return initializer;
  }
}
