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
