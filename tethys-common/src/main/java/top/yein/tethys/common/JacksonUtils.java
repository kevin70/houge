package top.yein.tethys.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 9:47
 */
public class JacksonUtils {

  private JacksonUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * 全局的 {@link ObjectMapper}.
   *
   * @return ObjectMapper
   */
  public static ObjectMapper objectMapper() {
    return OBJECT_MAPPER;
  }

  /**
   * @param value
   * @return
   */
  public static ByteBuf writeAsByteBuf(Object value) {
    var buf = ByteBufAllocator.DEFAULT.buffer();
    writeValue(buf, value);
    return buf;
  }

  /**
   * @param value
   * @return
   */
  public static ByteBuf writeAsDirectByteBuf(Object value) {
    var buf = ByteBufAllocator.DEFAULT.directBuffer();
    writeValue(buf, value);
    return buf;
  }

  private static void writeValue(ByteBuf buf, Object value) {
    OutputStream out = new ByteBufOutputStream(buf);
    try {
      OBJECT_MAPPER.writeValue(out, value);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
