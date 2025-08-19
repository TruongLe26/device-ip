package dev.rlet.reactivedevicee.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.r2dbc.device")
public class DeviceDataSourceProperties {
  @NotBlank private String host;
  @NotNull private Integer port;
  @NotBlank private String database;
  @NotBlank private String username;
  @NotBlank private String password;
}
