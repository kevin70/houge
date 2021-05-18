/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cool.houge.domain;

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
