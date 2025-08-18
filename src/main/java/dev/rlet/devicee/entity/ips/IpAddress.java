package dev.rlet.devicee.entity.ips;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ips")
@Table(name = "ip_pool")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imsi;

  private String ip;
}
