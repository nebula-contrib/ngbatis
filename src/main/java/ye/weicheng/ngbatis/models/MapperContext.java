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

    private static MapperContext INSTANCE;

    private MapperContext() {
    }

    public static MapperContext newInstance() {
        if( INSTANCE == null ) {
            INSTANCE = new MapperContext();
        }
        return INSTANCE;
    }

    Map<String, ClassModel> interfaces;

    Map<String, String> daoBasicTpl;

    DataSource dataSource;

    NebulaPool nebulaPool;

    boolean resourceRefresh = false;

    public Map<String, ClassModel> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<String, ClassModel> interfaces) {
        this.interfaces = interfaces;
    }

    public Map<String, String> getDaoBasicTpl() {
        return daoBasicTpl;
    }

    public void setDaoBasicTpl(Map<String, String> daoBasicTpl) {
        this.daoBasicTpl = daoBasicTpl;
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
