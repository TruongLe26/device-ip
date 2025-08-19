package dev.rlet.reactivedevicee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpAddress {

  @Id private Long id;

  private String imsi;

  private String ip;
}
