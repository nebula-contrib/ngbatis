package org.nebula.contrib.ngbatis.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**.
 * yml 配置文件对应属性的模型类.
 *.
 * @author yeweicheng.
 * @since 2022-06-09 11:16 <br>.
 *     Now is history.
.*/
@Component
@ConfigurationProperties(prefix = "nebula")
public class NebulaJdbcProperties {

  public NebulaJdbcProperties() { }

  /** Nebula 地址。由 hosts 切割而来.*/
  private List<HostAddress> hostAddresses;

  /** Nebula 多个库的地址。格式：ip:port, ip:port, ip:port, .....*/
  private String hosts;

  /** Nebula 连接配置.*/
  private NebulaPoolConfig poolConfig;
  /** 数据库用户名.*/
  private String username;
  /** 数据库密码.*/
  private String password;
  /** 当前所有的数据库空间.*/
  private String space;

  public List<HostAddress> getHostAddresses() {
    return hostAddresses;
  }

  public String getHosts() {
    return hosts;
  }

  /**.
   * 对 hosts 进行切割.
   *.
   * @param hosts ip:port, ip:port, ....
   * @return 当前对象，方便链式调用.
  .*/
  public NebulaJdbcProperties setHosts(final String hosts) {
    String[] hostArr = hosts.split(",");
    hostAddresses = new ArrayList<HostAddress>();
    for (int i = 0; i < hostArr.length; i++) {
      String ipAndPort = hostArr[i];
      String[] iandp = ipAndPort.split(":");
      hostAddresses.add(
        new HostAddress(iandp[0].trim(), Integer.parseInt(iandp[1].trim())));
    }
    this.hosts = hosts;
    return this;
  }

  public NebulaPoolConfig getPoolConfig() {
    return poolConfig;
  }

  public NebulaJdbcProperties setPoolConfig(
    final NebulaPoolConfig poolConfig) {
      this.poolConfig = poolConfig;
      return this;
    }

  public String getUsername() {
    return username;
  }

  public NebulaJdbcProperties setUsername(final String username) {
    this.username = username;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public NebulaJdbcProperties setPassword(final String password) {
    this.password = password;
    return this;
  }

  public String getSpace() {
    return space;
  }

  public NebulaJdbcProperties setSpace(final String space) {
    this.space = space;
    return this;
  }
}
