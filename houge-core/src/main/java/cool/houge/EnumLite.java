/*
 * Copyright 2019-2020 the original author or authors
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
package cool.houge;

/**
 * 在字段集中使用的枚举值或数值值描述符的接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface EnumLite {

  /**
   * 返回枚举数值.
   *
   * @return 数值
   */
  int getCode();
}
