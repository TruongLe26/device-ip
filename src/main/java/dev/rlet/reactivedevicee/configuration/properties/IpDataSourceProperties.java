package dev.rlet.reactivedevicee.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.r2dbc.ip")
public class IpDataSourceProperties {
  @NotBlank private String protocol;
  @NotBlank private String database;
  @NotBlank private String username;
  @NotBlank private String password;
}
