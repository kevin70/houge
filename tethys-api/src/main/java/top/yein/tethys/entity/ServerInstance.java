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
package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IM 服务实例信息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInstance {

  /** 主键 ID. */
  private int id;
  /** 应用名称. */
  private String appName;
  /** 主机名. */
  private String hostName;
  /** 主机 IP 地址. */
  private String hostAddress;
  /** 系统名称. */
  private String osName;
  /** 系统版本. */
  private String osVersion;
  /** OS Arch. */
  private String osArch;
  /** 系统用户. */
  private String osUser;
  /** Java 虚拟机名称. */
  private String javaVmName;
  /** Java 虚拟机版本. */
  private String javaVmVersion;
  /** Java 虚拟机供应商. */
  private String javaVmVendor;
  /** 服务工作目录. */
  private String workDir;
  /** 进程 ID. */
  private long pid;
  /** 数据版本. */
  private int ver;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 最后检查时间. */
  private LocalDateTime checkTime;
}
