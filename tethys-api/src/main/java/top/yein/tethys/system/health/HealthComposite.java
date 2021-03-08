package top.yein.tethys.system.health;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Collection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 应用健康状况复合对象.
 *
 * @author KK (kzou227@qq.com)
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class HealthComposite {

  /** 应用健康状况. */
  @JsonUnwrapped
  private HealthStatus status;
  /** 应用所有组件健康状况. */
  private Collection<Health> components;
}
