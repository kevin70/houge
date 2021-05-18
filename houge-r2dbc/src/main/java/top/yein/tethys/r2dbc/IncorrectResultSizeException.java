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
package top.yein.tethys.r2dbc;

import io.r2dbc.spi.R2dbcException;

/**
 * R2DBC 错误的结果数量异常.
 *
 * @author KK (kzou227@qq.com)
 */
public class IncorrectResultSizeException extends R2dbcException {

  private final int expectedSize;

  /**
   * 使用预期结果数量构建异常.
   *
   * @param expectedSize 预期结果数量
   */
  public IncorrectResultSizeException(int expectedSize) {
    super("Incorrect result size: expected " + expectedSize);
    this.expectedSize = expectedSize;
  }

  /**
   * 使用描述和预期结果数据构建异常.
   *
   * @param message 描述
   * @param expectedSize 预期结果数量
   */
  public IncorrectResultSizeException(String message, int expectedSize) {
    super(message);
    this.expectedSize = expectedSize;
  }

  /**
   * 返回预期结果数量.
   *
   * @return 预期结果数量
   */
  public int getExpectedSize() {
    return expectedSize;
  }
}
