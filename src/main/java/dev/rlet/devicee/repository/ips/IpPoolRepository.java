package dev.rlet.devicee.repository.ips;

import dev.rlet.devicee.entity.ips.IpAddress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpPoolRepository extends JpaRepository<IpAddress, Long> {
  Optional<IpAddress> findByImsi(String imsi);
}
