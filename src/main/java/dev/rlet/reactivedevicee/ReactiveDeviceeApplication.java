package dev.rlet.reactivedevicee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class ReactiveDeviceeApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReactiveDeviceeApplication.class, args);
  }
}
