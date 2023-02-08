package org.nebula.contrib.ngbatis.models;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import org.nebula.contrib.ngbatis.config.NgbatisConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xml 中标签所声明的信息（方法）
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class MapperContext {

  private static MapperContext INSTANCE;
  /**
   * 标签名与实体的映射。<br> （除了根据 {@link org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic } 中 泛型 T
   * 所自动识别的外，还可以自行补充）
   */
  final Map<String, Class<?>> tagTypeMapping = new HashMap<>();
  /**
   * 当前应用中，在 xml 中 namespace 所声明的所有 XXXDao 及其 类模型
   */
  Map<String, ClassModel> interfaces;
  /**
   * 基类所有的 nGQL模板
   */
  Map<String, String> daoBasicTpl;
  /**
   * Nebula 连接池
   */
  NebulaPool nebulaPool;
  /**
   * Nebula space name set
   */
  Set<String> spaceNameSet = new HashSet<>();
  /**
   * Nebula SessionPool map
   * key: spaceName
   */
  Map<String, SessionPool> nebulaSessionPoolMap = new ConcurrentHashMap<>();
  /**
   * Nebula 连接配置
   */
  NebulaPoolConfig nebulaPoolConfig;
  /**
   * ngbatis 扩展参数
   */
  private NgbatisConfig ngbatisConfig;
  boolean resourceRefresh = false;

  private MapperContext() {
  }

  /**
   * <p>创建mapper上下文单例。</p>
   * @return 上下问实例
   */
  public static MapperContext newInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MapperContext();
    }
    return INSTANCE;
  }

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

  public NebulaPool getNebulaPool() {
    return nebulaPool;
  }

  public void setNebulaPool(NebulaPool nebulaPool) {
    this.nebulaPool = nebulaPool;
  }

  public Set<String> getSpaceNameSet() {
    return spaceNameSet;
  }

  public void setSpaceNameSet(Set<String> spaceNameSet) {
    this.spaceNameSet = spaceNameSet;
  }

  public Map<String, SessionPool> getNebulaSessionPoolMap() {
    return nebulaSessionPoolMap;
  }

  public void setNebulaSessionPoolMap(Map<String, SessionPool> nebulaSessionPoolMap) {
    this.nebulaSessionPoolMap = nebulaSessionPoolMap;
  }

  public NebulaPoolConfig getNebulaPoolConfig() {
    return nebulaPoolConfig;
  }

  public void setNebulaPoolConfig(NebulaPoolConfig nebulaPoolConfig) {
    this.nebulaPoolConfig = nebulaPoolConfig;
  }

  public NgbatisConfig getNgbatisConfig() {
    return ngbatisConfig;
  }

  public void setNgbatisConfig(NgbatisConfig ngbatisConfig) {
    this.ngbatisConfig = ngbatisConfig;
  }

  public boolean isResourceRefresh() {
    return resourceRefresh;
  }

  public void setResourceRefresh(boolean resourceRefresh) {
    this.resourceRefresh = resourceRefresh;
  }

  public Map<String, Class<?>> getTagTypeMapping() {
    return tagTypeMapping;
  }

}
