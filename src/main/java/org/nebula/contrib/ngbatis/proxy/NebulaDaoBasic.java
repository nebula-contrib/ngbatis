package org.nebula.contrib.ngbatis.proxy;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.*;

import com.vesoft.nebula.client.graph.data.ResultSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.nebula.contrib.ngbatis.exception.QueryException;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.Page;

/**
 * 数据访问的基类，用于提供单表 CRUD 与基本的节点关系操作<br>
 * <strong>以下在方法注释中所说的“对应类型” 均指的是 泛 型T</strong>
 *
 * @author yeweicheng
 * @since 2022-06-12 12:21 <br>
 *     Now is history!
 */
public interface NebulaDaoBasic<T, ID extends Serializable> {
  /**
   * 数据操作，逻辑删除接口，前提当前类 有字段 is_del
   *
   * @param id 表记录主键
   * @return 是否执行成功，成功 1 ，失败 0
   */
  default int deleteLogicById(ID id) {
    throw new QueryException("No implements");
  }

  /**
   * 数据操作，根据节点 id 将节点连同其连接的关系一同物理删除
   *
   * @param id 表记录主键
   * @return 是否执行成功，成功 1 ，失败 0
   */
  default int deleteWithEdgeById(ID id) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(methodModel, id);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * 通过 主键删除当前记录
   *
   * @param id 表记录主键
   * @return 是否删除成功，成功 1，失败 0
   */
  default int deleteById(ID id) {
    throw new QueryException("No implements");
  }

  /**
   * 插入一条记录，全属性插入
   *
   * @param record 当前表对应的记录数据
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer insert(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(methodModel, record);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * 插入非空字段。
   *
   * @param record 单个顶点
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer insertSelective(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(methodModel, record);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * 通过主键查询对应表的单条记录
   *
   * @param id 记录主键
   * @return 表中的记录对应的实体对象
   */
  default T selectById(ID id) {
    MethodModel methodModel = getMethodModel();
    Class<?> currentType = this.getClass();
    methodModel.setReturnType(Collection.class);
    methodModel.setResultType(entityType(currentType));
    return (T) MapperProxy.invoke(methodModel, id);
  }

  /**
   * 通过多个 id 值查询符合条件的记录
   *
   * @param ids 多个 id
   * @return 多个 id 对应的节点
   */
  default List<T> selectByIds(Collection<ID> ids) {

    throw new QueryException("No implements");
  }

  /**
   * 以实体类为载体，存放查询条件，不为空的属性为查询条件
   *
   * @param record 单个节点做为查询条件
   * @return 符合条件节点的集合
   */
  default List<T> selectBySelective(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(List.class);
    methodModel.setResultType(entityType(this.getClass()));
    return (List<T>) MapperProxy.invoke(methodModel, record);
  }

  /**
   * 以实体类为载体，存放查询条件，不为空的属性为查询条件，String 类型的属性也使用精确查询
   *
   * @param record 查询条件
   * @return 符合条件的节点集合
   */
  default List<T> selectBySelectivePrecise(T record) {

    throw new QueryException("No implements");
  }

  /**
   * 通过 map 存放查询参数，查询多条记录并映射成实体类
   *
   * <p>通常与 {@link #countByMap(Map) countByMap} 联合使用，以实现分页数据获取功能
   *
   * @param param 查询条件
   * @return 符合查询条件的节点集合
   */
  default List<T> selectByMap(Map<String, Object> param) {

    throw new QueryException("No implements");
  }

  /**
   * 统计符合条件的记录数
   *
   * <p>通常与 {@link #selectByMap(Map) selectByMap} 联合使用，以实现分页数据获取功能
   *
   * @param param 查询条件
   * @return 统及符合查询条件的总节点数
   */
  default Long countByMap(Map<String, Object> param) {

    throw new QueryException("No implements");
  }

  /**
   * 按条件查出所有符合条件的记录的 主键
   *
   * @param record 查询条件
   * @return 符合查询条件的节点 id
   */
  default List<ID> selectIdBySelective(T record) {

    throw new QueryException("No implements");
  }

  /**
   * 更新
   *
   * @param record 节点
   * @return 是否删除成功，成功 1，失败 0
   */
  default int updateByIdSelective(T record) {

    throw new QueryException("No implements");
  }

  default int updateByIdWithBLOBs(T record) {

    throw new QueryException("No implements");
  }

  default int updateById(T record) {

    throw new QueryException("No implements");
  }

  default int insertBatch(List<T> ts) {

    throw new QueryException("No implements");
  }

  default Long countGridByMap(Map<String, Object> param) {

    throw new QueryException("No implements");
  }

  default List<Map> selectGridByMap(Map<String, Object> param) {

    throw new QueryException("No implements");
  }

  default int updateSelective(T t) {

    throw new QueryException("No implements");
  }

  default Long countPage(Page<T> page) {
    MethodModel methodModel = getMethodModel();
    return (Long) MapperProxy.invoke(methodModel, page);
  }

  /**
   * 查询对应类型的数据并分页
   *
   * @param page 分页的参数，与分页结果的容器
   * @return 分页的结果
   */
  default List<T> selectPage(Page<T> page) {
    MethodModel methodModel = getMethodModel();
    Long total = countPage(page);
    page.setTotal(total);
    if (total == 0) return Collections.EMPTY_LIST;
    ;
    methodModel.setReturnType(List.class);
    methodModel.setResultType(entityType(this.getClass()));
    List<T> proxy = (List<T>) MapperProxy.invoke(methodModel, page);
    page.setRows(proxy);
    return proxy;
  }

  default int updateBatch(List<T> ts) {

    throw new QueryException("No implements");
  }

  /**
   * 根据三元组值，插入关系
   *
   * @param v1 开始节点值
   * @param e 关系值
   * @param v2 结束节点值
   */
  default void insertEdge(Object v1, Object e, Object v2) {
    if (v2 == null || v1 == null || e == null) return;
    MethodModel methodModel = getMethodModel();
    MapperProxy.invoke(methodModel, v1, e, v2);
  }

  /**
   * 提供开始节点的id、结束节点的id 与 关系名，判断是否已经建立关系
   *
   * @param startId 开始节点的 id
   * @param edgeType 关系类型
   * @param endId 结束节点的 id
   * @return 数据库中，两个 id 的节点是否有关系
   */
  default Boolean existsEdge(ID startId, Class<?> edgeType, ID endId) {
    String cqlTpl = getCqlTpl();
    String edgeName = edgeName(edgeType);
    return (Boolean)
        proxy(
            this.getClass(),
            Boolean.class,
            cqlTpl,
            new Class[] {Serializable.class, Class.class, Serializable.class},
            startId,
            edgeName,
            endId);
  }

  /**
   * 通过结束节点id与关系类型获取所有开始节点，<br>
   * 开始节点类型为当前接口实现类所管理的实体对应的类型
   *
   * @param edgeType 关系类型
   * @param endId 结束节点的 id
   * @return 开始节点
   */
  default List<T> listStartNodes(Class<?> edgeType, ID endId) {
    Class<?> startType = entityType(this.getClass());
    return (List<T>) listStartNodes(startType, edgeType, endId);
  }

  /**
   * 指定开始节点类型，并通过结束节点id与关系类型获取所有开始节点
   *
   * @param startType 开始节点的类型
   * @param edgeType 关系类型
   * @param endId 结束节点的 id
   * @return 开始节点
   */
  default List<?> listStartNodes(
      Class<?> startType, Class<?> edgeType, ID endId) {
    String cqlTpl = getCqlTpl();
    String startVertexName = vertexName(startType);
    String edgeName = edgeName(edgeType);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    Class<?> returnType = entityType(daoType);
    return (List<?>)
        proxy(
            daoType,
            returnType,
            cqlTpl,
            new Class[] {Class.class, Class.class, Serializable.class},
            startVertexName,
            edgeName,
            endId);
  }

  /**
   * 通过结束节点id与关系类型获取第一个开始节点，<br>
   * 开始节点类型为当前接口实现类所管理的实体对应的类型 （对应类型）
   *
   * @param edgeType 关系类型
   * @param endId 结束节点的 id
   * @return 开始节点
   */
  default T startNode(Class<?> edgeType, ID endId) {
    Class<?> startType = entityType(this.getClass());
    return (T) startNode(startType, edgeType, endId);
  }

  /**
   * 指定开始节点类型，并通过结束节点id与关系类型获取第一个开始节点
   *
   * @param startType 开始节点的类型
   * @param edgeType 关系类型
   * @param endId 结束节点的 id
   * @param <E> 开始节点的类型
   * @return 开始节点
   */
  default <E> E startNode(Class<E> startType, Class<?> edgeType, ID endId) {
    String cqlTpl = getCqlTpl();
    String startVertexName = vertexName(startType);
    String edgeName = edgeName(edgeType);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    Class<?> returnType = entityType(daoType);
    return (E)
        proxy(
            daoType,
            returnType,
            cqlTpl,
            new Class[] {Class.class, Class.class, Serializable.class},
            startVertexName,
            edgeName,
            endId);
  }
}
