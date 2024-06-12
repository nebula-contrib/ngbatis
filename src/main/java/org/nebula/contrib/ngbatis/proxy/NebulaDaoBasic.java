// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

package org.nebula.contrib.ngbatis.proxy;

import com.sun.istack.NotNull;
import com.vesoft.nebula.client.graph.data.ResultSet;
import org.nebula.contrib.ngbatis.exception.QueryException;
import org.nebula.contrib.ngbatis.handler.CollectionStringResultHandler;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.utils.Page;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.*;

import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.*;

/**
 * 数据访问的基类，用于提供单表 CRUD 与基本的节点关系操作<br>
 * <strong>以下在方法注释中所说的“对应类型” 均指的是 泛 型T</strong>
 *
 * @author yeweicheng
 * @since 2022-06-12 12:21
 * <br>Now is history!
 */
public interface NebulaDaoBasic<T, I extends Serializable> {

  // region query zoom

  /**
   * <p>通过主键查询对应表的单条记录</p>
   *
   * @param id 记录主键
   * @return 表中的记录对应的实体对象
   */
  default T selectById(@Param("id") I id) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    return (T) MapperProxy.invoke(classModel, methodModel, id);
  }

  /**
   * <p>通过多个 id 值查询符合条件的记录</p>
   *
   * @param ids 多个 id
   * @return 多个 id 对应的节点
   */
  default List<T> selectByIds(@Param("ids") Collection<I> ids) {
    MethodModel methodModel = getMethodModel();
    Class<?> currentType = this.getClass();
    Class<?> entityType = entityType(currentType);
    methodModel.setResultType(entityType);
    ClassModel classModel = getClassModel(this.getClass());
    return (List<T>) MapperProxy.invoke(classModel, methodModel, ids);
  }

  /**
   * <p>以实体类为载体，存放查询条件，不为空的属性为查询条件</p>
   *
   * @param record 单个节点做为查询条件
   * @return 符合条件节点的集合
   */
  default List<T> selectBySelective(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(List.class);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(entityType(daoType));
    ClassModel classModel = getClassModel(daoType);
    return (List<T>) MapperProxy.invoke(classModel, methodModel, record);
  }

  /**
   * <p>以实体类为载体，存放查询条件，不为空的属性为查询条件，String 类型的属性使用模糊查询</p>
   *
   * @param record 查询条件
   * @return 符合条件的节点集合
   */
  default List<T> selectBySelectiveStringLike(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(List.class);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(entityType(daoType));
    ClassModel classModel = getClassModel(daoType);
    return (List<T>) MapperProxy.invoke(classModel, methodModel, record);
  }

  /**
   * <p>按条件查出所有符合条件的记录的 主键 </p>
   *
   * @param record 查询条件
   * @return 符合查询条件的节点 id
   */
  default List<I> selectIdBySelective(T record) {
    MethodModel methodModel = getMethodModel();
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(pkType(daoType));
    ClassModel classModel = getClassModel(daoType);
    return (List<I>) MapperProxy.invoke(classModel, methodModel, record);
  }

  /**
   * <p>按条件查出所有符合条件的记录的 主键 （String字段模糊查询）</p>
   *
   * @param record 查询条件
   * @return 符合查询条件的节点 id
   */
  default List<I> selectIdBySelectiveStringLike(T record) {
    MethodModel methodModel = getMethodModel();
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(pkType(daoType));
    ClassModel classModel = getClassModel(daoType);
    return (List<I>) MapperProxy.invoke(classModel, methodModel, record);
  }

  /**
   * <p>通过 map 存放查询参数，查询多条记录并映射成实体类</p>
   *
   * @param param 查询条件
   * @return 符合查询条件的节点集合
   */
  default List<T> selectByMap(Map<String, Object> param) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(List.class);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(entityType(daoType));
    ClassModel classModel = getClassModel(daoType);
    return (List<T>) MapperProxy.invoke(classModel, methodModel, param);
  }

  /**
   * <p>统计符合条件的记录数</p>
   *
   * @param param 查询条件
   * @return 统及符合查询条件的总节点数
   */
  default Long countByMap(Map<String, Object> param) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    return (Long) MapperProxy.invoke(classModel, methodModel, param);
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
    if (total == 0) {
      return Collections.EMPTY_LIST;
    }
    methodModel.setReturnType(List.class);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    methodModel.setResultType(entityType(daoType));
    ClassModel classModel = getClassModel(daoType);
    List<T> proxy = (List<T>) MapperProxy.invoke(classModel, methodModel, page);
    page.setRows(proxy);
    return proxy;
  }

  default Long countPage(Page<T> page) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    return (Long) MapperProxy.invoke(classModel, methodModel, page);
  }
  // endregion

  // region insert zoom

  /**
   * <p>插入一条记录，全属性插入</p>
   *
   * @param record 当前表对应的记录数据
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer insert(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ClassModel classModel = getClassModel(this.getClass());
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(classModel, methodModel, record);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * <p>插入非空字段。</p>
   *
   * @param record 单个顶点
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer insertSelective(T record) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ClassModel classModel = getClassModel(this.getClass());
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(classModel, methodModel, record);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * 批量插入全字段
   *
   * @param ts 当前Tag下的多节点
   */
  default void insertBatch(List<T> ts) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, ts);
  }

  /**
   * 批量插入非空字段
   *
   * @param ts 当前Tag下多个顶点
   */
  default void insertSelectiveBatch(List<? extends T> ts) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, ts);
  }
  // endregion

  // region update zoom
  default T updateById(T record) {
    MethodModel methodModel = getMethodModel();
    Class<?> entityType = record.getClass();
    methodModel.setReturnType(entityType);
    methodModel.setResultType(entityType);
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, record);
    return record;
  }

  /**
   * <p>更新</p>
   *
   * @param record 节点
   * @return 原参数对象
   */
  default T updateByIdSelective(T record) {
    MethodModel methodModel = getMethodModel();
    Class<?> entityType = record.getClass();
    methodModel.setReturnType(entityType);
    methodModel.setResultType(entityType);
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, record);
    return record;
  }

  /**
   * 批量更新行记录，selective
   *
   * @param ts 当前Tag下的多节点
   */
  default void updateByIdBatchSelective(List<T> ts) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, ts);
  }

  /**
   * <p>新增/更新</p>
   * <p>Selective: 仅处理非空字段</p>
   *
   * @param record 节点
   */
  default void upsertByIdSelective(T record) {
    MethodModel methodModel = getMethodModel();
    Class<?> entityType = record.getClass();
    methodModel.setReturnType(entityType);
    methodModel.setResultType(entityType);
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, record);
  }
  // endregion

  // region delete zoom

  /**
   * <p>数据操作，逻辑删除接口，前提当前类 有字段 is_del </p>
   *
   * @param id 表记录主键
   * @return 是否执行成功，成功 1 ，失败 0
   */
  default int deleteLogicById(I id) {
    throw new QueryException("No implements");
  }

  /**
   * <p>数据操作，根据节点 id 将节点连同其连接的关系一同物理删除</p>
   *
   * @param id 表记录主键
   * @return 是否执行成功，成功 1 ，失败 0
   */
  default Integer deleteWithEdgeById(I id) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ClassModel classModel = getClassModel(this.getClass());
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(classModel, methodModel, id);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  /**
   * <p>通过 主键删除当前记录</p>
   *
   * @param id 表记录主键
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer deleteById(I id) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ClassModel classModel = getClassModel(this.getClass());
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(classModel, methodModel, id);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  // endregion

  /**
   * <p>通过 主键批量删除当前记录</p>
   *
   * @param ids 表记录主键列表
   * @return 是否删除成功，成功 1，失败 0
   */
  default Integer deleteByIdBatch(Collection<I> ids) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(ResultSet.class);
    methodModel.setResultType(ResultSet.class);
    ClassModel classModel = getClassModel(this.getClass());
    ResultSet resultSet = (ResultSet) MapperProxy.invoke(classModel, methodModel, ids);
    return resultSet.isSucceeded() ? 1 : 0;
  }

  // region graph special

  /**
   * 根据三元组值，插入关系
   *
   * @param v1 开始节点值 或 开始节点id
   * @param e  关系值
   * @param v2 结束节点值 或 结束节点id
   */
  default void insertEdge(@NotNull Object v1, @NotNull Object e, @NotNull Object v2) {
    if (v2 == null || v1 == null || e == null) {
      return;
    }
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, v1, e, v2);
  }

  /**
   * @Author sunhb
   * @Description 根据三元组列表的头结点，尾节点和尾节点进行插入
   * @Date 2023/10/10 上午11:03
   **/
  default void insertEdgeBatch(List triplets) {
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, triplets);
  }

  /**
   * 根据三元组值, 插入关系
   * <p>Selective: 仅处理非空字段</p>
   *
   * @param src  开始节点值
   * @param edge 关系值
   * @param dst  结束节点值
   */
  default void insertEdgeSelective(@NotNull Object src, @NotNull Object edge, @NotNull Object dst) {
    if (dst == null || src == null || edge == null) {
      return;
    }
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, src, edge, dst);
  }

  /**
   * 根据三元组值, 插入关系
   * <p>Selective: 仅处理非空字段</p>
   *
   * @param src  开始节点值
   * @param edge 关系值
   * @param dst  结束节点值
   */
  default void upsertEdgeSelective(@NotNull Object src, @NotNull Object edge, @NotNull Object dst) {
    if (dst == null || src == null || edge == null) {
      return;
    }
    MethodModel methodModel = getMethodModel();
    ClassModel classModel = getClassModel(this.getClass());
    MapperProxy.invoke(classModel, methodModel, src, edge, dst);
  }

  /**
   * 提供开始节点的id、结束节点的id 与 关系名，判断是否已经建立关系
   *
   * @param startId  开始节点的 id
   * @param edgeType 关系类型
   * @param endId    结束节点的 id
   * @return 数据库中，两个 id 的节点是否有关系
   */
  default Boolean existsEdge(I startId, Class<?> edgeType, I endId) {
    String cqlTpl = getCqlTpl();
    String edgeName = edgeName(edgeType);
    return (Boolean) proxy(this.getClass(), Boolean.class, cqlTpl,
            new Class[]{Serializable.class, Class.class, Serializable.class}, startId, edgeName,
            endId);
  }

  /**
   * 通过结束节点id与关系类型获取所有开始节点，<br> 开始节点类型为当前接口实现类所管理的实体对应的类型
   *
   * @param edgeType 关系类型
   * @param endId    结束节点的 id
   * @return 开始节点列表
   */
  default List<T> listStartNodes(Class<?> edgeType, I endId) {
    Class<?> startType = entityType(this.getClass());
    return (List<T>) listStartNodes(startType, edgeType, endId);
  }

  /**
   * 指定开始节点类型，并通过结束节点id与关系类型获取所有开始节点
   *
   * @param startType 开始节点的类型
   * @param edgeType  关系类型
   * @param endId     结束节点的 id
   * @return 开始节点列表
   */
  default List<?> listStartNodes(Class<?> startType, Class<?> edgeType, I endId) {
    String cqlTpl = getCqlTpl();
    String startVertexName = vertexName(startType);
    String edgeName = edgeName(edgeType);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    Class<?> returnType = entityType(daoType);
    return (List<?>) proxy(daoType, returnType, cqlTpl,
            new Class[]{Class.class, Class.class, Serializable.class}, startVertexName, edgeName,
            endId);
  }

  /**
   * 通过开始节点id与关系类型获取所有结束节点，<br> 结束节点类型为当前接口实现类所管理的实体对应的类型
   *
   * @param startId  开始节点id
   * @param edgeType 关系类型
   * @return 结束节点列表
   */
  default List<T> listEndNodes(I startId, Class<?> edgeType) {
    Class<?> endType = entityType(this.getClass());
    return (List<T>) listEndNodes(startId, edgeType, endType);
  }

  /**
   * 指定结束节点类型 并通过开始节点id与关系类型获取所有结束节点
   *
   * @param startId  开始节点的id
   * @param edgeType 关系类型
   * @param endType  结束节点的类型
   * @return 结束节点列表
   */
  default List<?> listEndNodes(I startId, Class<?> edgeType, Class<?> endType) {
    String cqlTpl = getCqlTpl();
    String endVertexName = vertexName(endType);
    String edgeName = edgeName(edgeType);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    Class<?> returnType = entityType(daoType);
    return (List<?>) proxy(daoType, returnType, cqlTpl,
            new Class[]{Serializable.class, Class.class, Class.class}, startId, edgeName,
            endVertexName);
  }

  /**
   * 通过结束节点id与关系类型获取第一个开始节点，<br> 开始节点类型为当前接口实现类所管理的实体对应的类型 （对应类型）
   *
   * @param edgeType 关系类型
   * @param endId    结束节点的 id
   * @return 开始节点
   */
  default T startNode(Class<?> edgeType, I endId) {
    Class<?> startType = entityType(this.getClass());
    return (T) startNode(startType, edgeType, endId);
  }

  /**
   * 指定开始节点类型，并通过结束节点id与关系类型获取第一个开始节点
   *
   * @param startType 开始节点的类型
   * @param edgeType  关系类型
   * @param endId     结束节点的 id
   * @param <E>       开始节点的类型
   * @return 开始节点
   */
  default <E> E startNode(Class<E> startType, Class<?> edgeType, I endId) {
    String cqlTpl = getCqlTpl();
    String startVertexName = vertexName(startType);
    String edgeName = edgeName(edgeType);
    Class<? extends NebulaDaoBasic> daoType = this.getClass();
    Class<?> returnType = entityType(daoType);
    return (E) proxy(daoType, returnType, cqlTpl,
            new Class[]{Class.class, Class.class, Serializable.class}, startVertexName, edgeName,
            endId);
  }

  /**
   * Find the shortest path by srcId and dstId.
   *
   * @param srcId the start node's id
   * @param dstId the end node's id
   * @return Shortest path list. entities containing vertext in path.
   * If you want to obtain attributes within an entity,
   * you need to use “with prop” in the nGQL.
   */
  default List<NgPath<I>> shortestPath(@Param("srcId") I srcId, @Param("dstId") I dstId) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(Collection.class);
    methodModel.setResultType(NgPath.class);
    ClassModel classModel = getClassModel(this.getClass());
    return (List<NgPath<I>>) MapperProxy.invoke(classModel, methodModel, srcId, dstId);
  }

  /**
   * 查找指定起始点和目的点之间的最短路径
   *
   * @param srcId        起始点id
   * @param dstId        目的点id
   * @param edgeTypeList Edge type 列表
   * @param direction    REVERSELY表示反向，BIDIRECT表示双向
   * @return 起始点和目的点之间的最短路径
   */
  default List<NgPath<I>> shortestOptionalPath(@Param("srcId") I srcId, @Param("dstId") I dstId,
                                               @Param("edgeTypeList") List<String> edgeTypeList,
                                               @Param("direction") String direction
  ) {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(Collection.class);
    methodModel.setResultType(NgPath.class);
    ClassModel classModel = getClassModel(this.getClass());
    return (List<NgPath<I>>) MapperProxy.invoke(classModel, methodModel,
            srcId, dstId, edgeTypeList, direction);
  }

  // endregion


  /**
   * 列出所有图空间
   *
   * @return 所有图空间
   */
  default List<String> showSpaces() {
    MethodModel methodModel = getMethodModel();
    methodModel.setReturnType(CollectionStringResultHandler.class);
    methodModel.setResultType(String.class);
    ClassModel classModel = getClassModel(this.getClass());
    return (List<String>) MapperProxy.invoke(classModel, methodModel);
  }


}
