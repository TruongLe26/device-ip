package dev.rlet.reactivedevicee.testcontainers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Testcontainers
@DirtiesContext
public abstract class BaseTestcontainersTest {

  @Container
  static MySQLContainer<?> mysqlContainer =
      new MySQLContainer<>("mysql:8.0.33")
          .withDatabaseName("device")
          .withUsername("test")
          .withPassword("test")
          .withReuse(true)
          .withCommand("--default-authentication-plugin=mysql_native_password");

  //    @Container
  //    static GenericContainer<?> h2Container = new GenericContainer<>("oscarfonts/h2:latest")
  //            .withExposedPorts(1521, 81)
  //            .withEnv("H2_OPTIONS", "-ifNotExists");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.device.host", mysqlContainer::getHost);
    registry.add("spring.r2dbc.device.port", () -> mysqlContainer.getFirstMappedPort().toString());
    registry.add("spring.r2dbc.device.database", mysqlContainer::getDatabaseName);
    registry.add("spring.r2dbc.device.username", mysqlContainer::getUsername);
    registry.add("spring.r2dbc.device.password", mysqlContainer::getPassword);

    registry.add("spring.r2dbc.ip.protocol", () -> "mem");
    registry.add(
        "spring.r2dbc.ip.database",
        () -> "ipdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    registry.add("spring.r2dbc.ip.username", () -> "sa");
    registry.add("spring.r2dbc.ip.password", () -> "");
  }
}
