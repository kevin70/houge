package top.yein.tethys.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON jackson 工具包.
 *
 * @author KK (kzou227@qq.com)
 */
public class JsonUtils {

  private JsonUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    OBJECT_MAPPER.findAndRegisterModules();
  }

  /**
   * 全局的 {@link ObjectMapper}.
   *
   * @return ObjectMapper
   */
  public static ObjectMapper objectMapper() {
    return OBJECT_MAPPER;
  }
}
