package org.nebula.contrib.ngbatis.base;

import static org.nebula.contrib.ngbatis.base.GraphBaseExt.executeGql;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.getEdgeTypes;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.getV2Property;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.getV2Tag;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.handleResult;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.isPrimitiveDefaultValue;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getNameByColumn;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getValue;

import com.vesoft.nebula.client.graph.data.ResultSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nebula.contrib.ngbatis.annotations.base.GraphId;
import org.nebula.contrib.ngbatis.annotations.base.Tag;
import org.nebula.contrib.ngbatis.enums.Direction;
import org.nebula.contrib.ngbatis.enums.IdType;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgVertex;





/**
 * 点实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:02
 */

public abstract class GraphBaseVertex extends GraphBase {

  /**
   * 查询点的id
   * @return 点的id
   */
  public <T> List<T> queryIdsByProperties() {
    String textTpl = TextTplBuilder.matchVertexId();
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    IdType vertexIdType = getVertexIdType();
    Object res = null;
    if (vertexIdType == IdType.STRING) {
      res = handleResult(resultSet, List.class, String.class);
    } else if (vertexIdType == IdType.INT64) {
      res = handleResult(resultSet, List.class, Number.class);
    }
    if (res == null) {
      throw new RuntimeException("No matching vertex was found.");
    }
    return (List<T>) res;
  }

  /**
   * 根据点的id查询点
   * @return 符合条件的点实体
   */
  public <T extends GraphBaseVertex> T queryVertexById() {
    String textTpl = TextTplBuilder.fetchVertexById(getVertexIdType());
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("id", getVertexId());
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Object o = handleResult(resultSet, entityClass, null);
    return (T) o;
  }


  /**
   * 根据点的Tag查询点
   * @return 符合条件的点集合
   */
  public <T extends GraphBaseVertex> List<T> queryVertexByTag() {
    String textTpl = TextTplBuilder.matchVertexByTag();
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Object res = handleResult(resultSet, List.class, entityClass);
    return (List<T>) res;
  }


  /**
   * 查询点的属性
   * @return 符合条件的点集合
   */
  public <T extends GraphBaseVertex> List<T> queryVertexByProperties() {
    String textTpl = TextTplBuilder.matchVertexSelective();
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Object res = handleResult(resultSet, List.class, entityClass);
    return (List<T>) res;
  }

  /**
   * 查询所有邻点
   * @param edgeClass 边类型
   * @return 邻点集合
   */
  public <I> List<NgVertex<I>> queryAllAdjacentVertex(Class<?>... edgeClass) {
    String textTpl = TextTplBuilder.matchAllAdjacentVertex(getEdgeTypes("|", edgeClass));
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("edgeTypes", getEdgeTypes("|", edgeClass));
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
  }

  /**
   * 查询入边方向的邻点
   * @param edgeClass 边类型
   */
  public <I> List<NgVertex<I>> queryIncomingAdjacentVertex(Class<?>... edgeClass) {
    String textTpl = TextTplBuilder.matchIncomingVertex(getEdgeTypes("|", edgeClass));
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("edgeTypes", getEdgeTypes("|", edgeClass));
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
  }

  /**
   * 查询出边方向的邻点
   * @param edgeClass 边类型
   * @return 出边方向邻点集合
   */
  public <I> List<NgVertex<I>> queryOutgoingAdjacentVertex(Class<?>... edgeClass) {
    String textTpl = TextTplBuilder.matchOutgoingVertex(getEdgeTypes("|", edgeClass));
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("edgeTypes", getEdgeTypes("|", edgeClass));
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
  }


  /**
   * 查询一个点在指定跳数内的所有点
   * @param m 最大跳数
   * @param n 最小跳数
   * @param edgeClass 边类型
   * @return 符合条件的点集合
   */
  public <T> List<T> queryNeighborIdsWithHopById(Integer m, Integer n, Class<?>... edgeClass) {
    if (m < 0 || n < 0 || m > n) {
      throw new IllegalArgumentException("跳数范围不正确");
    }
    String textTpl = TextTplBuilder.goAdjacentVertexWithSteps(getEdgeTypes(",", edgeClass),
         getVertexIdType());
    Map<String, Object> args = new HashMap<>();
    args.put("m", m);
    args.put("n", n);
    args.put("id", getVertexId());
    args.put("edgeTypes", getEdgeTypes(",", edgeClass));
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    IdType vertexIdType = getVertexIdType();
    Object res = null;
    if (vertexIdType == IdType.STRING) {
      res = handleResult(resultSet, List.class, String.class);
    } else if (vertexIdType == IdType.INT64) {
      res = handleResult(resultSet, List.class, Number.class);
    }
    if (res == null) {
      throw new RuntimeException("No matching vertex was found.");
    }
    return (List<T>) res;
  }

  /**
   * 查询起始点关联的所有边
   * @param direction 边方向
   * @return 点关联的所有边
   */
  public <T> List<NgEdge<T>> queryConnectedEdgesById(Direction direction, Class<?>... edgeClass) {
    String textTpl = TextTplBuilder.goAllEdgesFromVertex(getEdgeTypes(",", edgeClass),
           getVertexIdType());
    Map<String, Object> args = new HashMap<>();
    args.put("id", getVertexId());
    args.put("direction", direction.getSymbol());
    args.put("edgeTypes", getEdgeTypes(",", edgeClass));
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    return (List<NgEdge<T>>) handleResult(resultSet, List.class, NgEdge.class);
  }

  /**
   * 查询符合条件的路径
   * @param direction 边的方向
   * @return 符合条件的路径
   */
  public <T> List<NgPath<T>> queryPathFromVertex(Direction direction) {
    String textTpl = TextTplBuilder.matchPath(direction);
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("properties", getEntityProperties());
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgPath<T>>) handleResult(resultSet, List.class, NgPath.class);
  }

  /**
   * 查询定长路径
   * @param maxHop 最大跳数
   * @param direction 方向
   * @param edgeClass 边类型
   * @return 符合条件的路径集合
   */
  public <I> List<NgPath<I>> queryFixedLengthPathFromVertex(Integer maxHop,
          Direction direction, Class<?>... edgeClass) {
    if (maxHop != null && maxHop < 0) {
      throw new RuntimeException("maxHop must be greater than or equal to zero");
    }
    String textTpl = TextTplBuilder.matchFixedLengthPath(maxHop, direction,
            getEdgeTypes("|", edgeClass));
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("properties", getEntityProperties());
    args.put("edgeTypes", getEdgeTypes("|", edgeClass));
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
  }

  /**
   * 查询变长路径
   * @param minHop 最小跳数
   * @param maxHop 最大跳数
   * @param direction 方向
   * @param edgeClass 边类型
   * @return 符合条件的路径集合
   */
  public <I> List<NgPath<I>> queryVariableLengthPathFromVertex(Integer minHop, Integer maxHop,
          Direction direction, Class<?> edgeClass) {
    if (maxHop != null && maxHop < 0) {
      throw new RuntimeException("maxHop must be greater than or equal to zero");
    }
    if (minHop != null && minHop < 0) {
      throw new RuntimeException("minHop must be greater than or equal to zero");
    }
    String textTpl = TextTplBuilder.matchVariableLengthPath(minHop, maxHop, direction,
           getEdgeTypes("|", edgeClass));
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    args.put("properties", getEntityProperties());
    args.put("edgeTypes", getEdgeTypes("|", edgeClass));
    ResultSet resultSet = executeGql(textTpl, args, getEntityProperties());
    return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
  }

  /**
   * 查询起始点到目标点任意一条最短路径
   * @param maxHop 路径最大长度
   * @param v2 终点
   * @return 符合条件的任意一条最短路径
   */
  public <T extends GraphBaseVertex, I> NgPath<I> queryShortestPathFromSrcAndDst(Integer maxHop,
          Direction direction, T v2) {
    if (maxHop != null && maxHop < 0) {
      throw new RuntimeException("maxHop must be greater than or equal to zero");
    }
    Map<String, Object> args = new HashMap<>();
    args.put("srcTag", getEntityTypeName());
    args.put("dstTag", getV2Tag(v2));
    args.put("srcProperties", getEntityProperties());
    args.put("dstProperties", getV2Property(v2));
    Map<String, Object> props = getEntityProperties();
    //加入终点属性参数，防止参数map的键重复
    getV2Property(v2).forEach((key, value) -> props.put("v2_" + key, value));
    String textTpl = TextTplBuilder.matchShortestPaths(maxHop, direction);
    ResultSet resultSet = executeGql(textTpl, args, props);
    return (NgPath<I>) handleResult(resultSet, NgPath.class, null);
  }

  /**
   * 查询起始点到目标点所有最短路径
   * @param maxHop 路径最大长度
   * @param direction 边类型
   * @param v2 终点
   * @return 符合条件的所有最短路径
   */
  public <T extends GraphBaseVertex, I> List<NgPath<I>> queryAllShortestPathsFromSrcAndDst(
          Integer maxHop, Direction direction, T v2) {
    if (maxHop != null && maxHop < 0) {
      throw new RuntimeException("maxHop must be greater than or equal to zero");
    }
    Map<String, Object> args = new HashMap<>();
    args.put("srcTag", getEntityTypeName());
    args.put("dstTag", getV2Tag(v2));
    args.put("srcProperties", getEntityProperties());
    args.put("dstProperties", getV2Property(v2));
    Map<String, Object> props = getEntityProperties();
    //加入终点属性参数，防止参数map的键重复
    getV2Property(v2).forEach((key, value) -> props.put("v2_" + key, value));
    String textTpl = TextTplBuilder.matchAllShortestPaths(maxHop, direction);
    ResultSet resultSet = executeGql(textTpl, args, props);
    return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
  }


  /**
   * 统计指定类型的点的个数
   * @return 点的个数
   */
  public Integer queryVertexCountByTag() {
    String textTpl = TextTplBuilder.lookupVertexCount();
    Map<String, Object> args = new HashMap<>();
    args.put("tag", getEntityTypeName());
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    return (Integer) handleResult(resultSet, Integer.class, null);
  }

  /**
   * 获取点的Tag
   */
  @Override
  protected String getEntityTypeName() {
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    if (entityClass.isAnnotationPresent(Tag.class)) {
      Tag tagAnnotation = entityClass.getAnnotation(Tag.class);
      return tagAnnotation.name();
    } else {
      throw new RuntimeException("Entity " + entityClass.getName()
              + " does not have @Tag annotation.");
    }
  }

  /**
   * 获取点的属性
   */
  @Override
  protected Map<String, Object> getEntityProperties() {
    Map<String, Object> properties = new HashMap<>();
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Object value = getValue(this, field);
      if (value == null) {
        continue;
      }
      // 如果是基本类型且是初始值，跳过
      if (field.getType().isPrimitive()) {
        if (isPrimitiveDefaultValue(field.getType(), value)) {
          continue; // 跳过初始值
        }
      }
      // 处理带有 @GraphId 注解的字段
      if (field.isAnnotationPresent(GraphId.class)) {
        properties.put("id", value);
      } else {
        properties.put(getNameByColumn(field), value);
      }
    }
    return properties;
  }

  /**
   * 获取点的id值
   * @return id值
   */
  private Object getVertexId() {
    Object id = null;
    Class<?> specificTypeField = null;
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof GraphId) {
          id = getValue(this, field);
          specificTypeField = field.getType();
        }
      }
    }
    if (id == null || isPrimitiveDefaultValue(specificTypeField,id)) {
      throw new RuntimeException(entityClass.getSimpleName() + " does not have @GraphId");
    }
    return id;
  }

  /**
   * 获取点的id类型
   */
  private IdType getVertexIdType() {
    Class<? extends GraphBaseVertex> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof GraphId) {
          return ((GraphId) annotation).type();
        }
      }
    }
    return IdType.STRING;
  }

}
