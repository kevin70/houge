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
package top.yein.tethys.system.identifier;

/**
 * 应用程序标识符接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ApplicationIdentifier {

  /**
   * 返回应用名称.
   *
   * @return 应用名称
   */
  String applicationName();

  /**
   * 返回应用标识 ID.
   *
   * <p>FID 应用实例在<b>集群</b>中的唯一标识符.
   *
   * @return 应用标识 ID
   */
  int fid();

  /**
   * 返回应用版本.
   *
   * @return 应用版本
   */
  String version();

  /** 清理应用数据. */
  void clean();
}
