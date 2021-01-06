package top.yein.tethys.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * <a href="https://tools.ietf.org/html/rfc7807">https://tools.ietf.org/html/rfc7807</a>.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
public class Problem {

  private String instance;
  private Integer status;
  private Integer code;
  private String title;
  private String detail;
  private Map<String, Object> properties;

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return properties;
  }
}
