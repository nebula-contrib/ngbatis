package org.nebula.contrib.ngbatis.base;

import static org.nebula.contrib.ngbatis.base.GraphBaseExt.executeGql;
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
import org.nebula.contrib.ngbatis.annotations.DstId;
import org.nebula.contrib.ngbatis.annotations.SrcId;
import org.nebula.contrib.ngbatis.annotations.base.EdgeType;
import org.nebula.contrib.ngbatis.enums.Direction;

/**
 * 边实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:01
 */

public abstract class GraphBaseEdge extends GraphBase {

  /**
   * 根据边的类型查询边
   * @param direction 边的方向
   * @return 指定类型的边的列表
   */
  public <T extends GraphBaseEdge> List<T> queryEdgeByType(Direction direction) {
    String textTpl = TextTplBuilder.matchEdgeByType(direction);
    Map<String,Object> args = new HashMap<>();
    args.put("edgeType",getEntityTypeName());
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    return (List<T>) handleResult(resultSet, List.class, entityClass);
  }

  /**
   * 根据边的属性查询符合条件的边
   * @param v1Vertex 点1
   * @param direction 边的方向
   * @param v2Vertex 点2
   * @return 符合条件的边列表
   */
  public <I extends GraphBaseEdge, T extends GraphBaseVertex> List<I>
    queryEdgeWithSrcAndDstByProperties(T v1Vertex, Direction direction, T v2Vertex) {
    Map<String,Object> args = new HashMap<>();
    args.put("edgeType",getEntityTypeName());
    args.put("edgeProperties",getEntityProperties());
    Map<String,Object> props = getEntityProperties();
    if (v1Vertex != null) {
      args.put("v1Tag",getV2Tag(v1Vertex));
      args.put("v1Properties", getV2Property(v1Vertex));
      getV2Property(v1Vertex).forEach((key, value) -> props.put("v1_" + key, value));
    }
    if (v2Vertex != null) {
      args.put("v2Tag",getV2Tag(v2Vertex));
      args.put("v2Properties", getV2Property(v2Vertex));
      getV2Property(v2Vertex).forEach((key, value) -> props.put("v2_" + key, value));
    }
    String textTpl = TextTplBuilder.matchEdgeWithEdgeProperty(direction);
    ResultSet resultSet = executeGql(textTpl, args, props);
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    return (List<I>) handleResult(resultSet, List.class, entityClass);
  }

  /**
   * 根据起始点id查找边
   * @return 指定起始点id的边
   */
  public <I extends GraphBaseEdge> I queryEdgePropertiesBySrcAndDstId() {
    String textTpl = TextTplBuilder.fetchEdgeProperty(getSrcId().get("type"),
             getDstId().get("type"));
    Map<String,Object> args = new HashMap<>();
    args.put("edgeType",getEntityTypeName());
    args.put("srcId",getSrcId().get("value"));
    args.put("dstId",getDstId().get("value"));
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    return (I) handleResult(resultSet, entityClass, null);
  }

  /**
   * 统计指定类型的边的个数
   * @return 边的个数
   */
  public Integer queryEdgeCountByType() {
    String textTpl = TextTplBuilder.lookupEdgeCount();
    Map<String,Object> args = new HashMap<>();
    args.put("tag",getEntityTypeName());
    ResultSet resultSet = executeGql(textTpl, args, new HashMap<>());
    return (Integer) handleResult(resultSet, Integer.class, null);
  }

  /**
   * 获取边的EdgeType
   */
  @Override
  protected String getEntityTypeName() {
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    if (entityClass.isAnnotationPresent(EdgeType.class)) {
      // 获取 @EdgeType 注解
      EdgeType edgeTypeAnnotation = entityClass.getAnnotation(EdgeType.class);
      // 获取 name 属性值
      return edgeTypeAnnotation.name();
    } else {
      throw new RuntimeException("Entity " + entityClass.getName()
              + " does not have @EdgeType annotation.");
    }
  }

  /**
   * 获取边的属性对
   */
  @Override
  protected Map<String, Object> getEntityProperties() {
    Map<String, Object> properties = new HashMap<>();
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Object value = getValue(this, field);
      if (value == null) {
        continue;
      }
      if (field.getType().isPrimitive()) {
        if (isPrimitiveDefaultValue(field.getType(), value)) {
          continue;
        }
      }
      properties.put(getNameByColumn(field), value);
    }
    return properties;
  }

  /**
   * 获取边的起点id值和type
   */
  private Map<String,Object> getSrcId() {
    Map<String,Object> result = new HashMap<>();
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof SrcId) {
          result.put("value",getValue(this, field));
          result.put("type",field.getType());
        }
      }
    }
    if (result.get("value") == null || isPrimitiveDefaultValue((Class<?>) result.get("type"),
           result.get("value"))) {
      throw new RuntimeException(entityClass.getSimpleName()
              + " does not have srcId.");
    }
    return result;
  }

  /**
   * 获取边的终点id值和type
   */
  private Map<String,Object> getDstId() {
    Map<String,Object> result = new HashMap<>();
    Class<? extends GraphBaseEdge> entityClass = this.getClass();
    Field[] declaredFields = entityClass.getDeclaredFields();
    for (Field field : declaredFields) {
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof DstId) {
          result.put("value",getValue(this, field));
          result.put("type",field.getType());
        }
      }
    }
    if (result.get("value") == null || isPrimitiveDefaultValue((Class<?>) result.get("type"),
            result.get("value"))) {
      throw new RuntimeException(entityClass.getSimpleName()
              + " does not have dstId.");
    }
    return result;
  }
}
