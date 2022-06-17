// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.config;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * yml 配置文件对应属性的模型类
 *
 * @author yeweicheng
 * @since 2022-06-09 11:16
 * <br>Now is history!
 */
@Component
@ConfigurationProperties(
        prefix = "nebula"
)
public class NebulaJdbcProperties {

    public NebulaJdbcProperties() {
    }

    private List<HostAddress> hostAddresses;

    private String hosts;

    private NebulaPoolConfig poolConfig;
    private String username;
    private String password;
    private String space;

    public List<HostAddress> getHostAddresses() {
        return hostAddresses;
    }

    public String getHosts() {
        return hosts;
    }

    public NebulaJdbcProperties setHosts(String hosts) {
        String[] hostArr = hosts.split(",");
        hostAddresses = new ArrayList<HostAddress>();
        for (int i = 0; i < hostArr.length; i++) {
            String ipAndPort = hostArr[i];
            String[] iandp =  ipAndPort.split(":");
            hostAddresses.add(new HostAddress(iandp[0].trim(), Integer.parseInt(iandp[1].trim())));
        }
        this.hosts = hosts;
        return this;
    }

    public NebulaPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public NebulaJdbcProperties setPoolConfig(NebulaPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public NebulaJdbcProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public NebulaJdbcProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSpace() {
        return space;
    }

    public NebulaJdbcProperties setSpace(String space) {
        this.space = space;
        return this;
    }

}

