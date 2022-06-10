// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.models;

import com.vesoft.nebula.client.graph.net.NebulaPool;

import javax.sql.DataSource;
import java.util.Map;

/**
 * xml 中标签所声明的信息（方法）
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class MapperContext {

    public MapperContext() {
    }

    public MapperContext(Map<String, ClassModel> interfaces, DataSource dataSource, NebulaPool nebulaPool, boolean resourceRefresh) {
        this.interfaces = interfaces;
        this.dataSource = dataSource;
        this.nebulaPool = nebulaPool;
        this.resourceRefresh = resourceRefresh;
    }

    Map<String, ClassModel> interfaces;

    DataSource dataSource;

    NebulaPool nebulaPool;

    boolean resourceRefresh = false;

    public Map<String, ClassModel> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<String, ClassModel> interfaces) {
        this.interfaces = interfaces;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public NebulaPool getNebulaPool() {
        return nebulaPool;
    }

    public void setNebulaPool(NebulaPool nebulaPool) {
        this.nebulaPool = nebulaPool;
    }

    public boolean isResourceRefresh() {
        return resourceRefresh;
    }

    public void setResourceRefresh(boolean resourceRefresh) {
        this.resourceRefresh = resourceRefresh;
    }
}
