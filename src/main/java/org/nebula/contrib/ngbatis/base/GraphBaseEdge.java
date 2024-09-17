package org.nebula.contrib.ngbatis.base;

import static org.nebula.contrib.ngbatis.base.GraphBaseExt.executeGQL;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.handleResult;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.lookupEdgeCount;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 边实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:01
 */

public abstract class GraphBaseEdge extends GraphBase {
  private Logger log = LoggerFactory.getLogger(GraphBaseVertex.class);

  /**
   * 根据边的类型查询边
   * @param direction 边的方向
   * @return 指定类型的边的列表
   */
  public <T extends GraphBaseEdge> List<T> queryByType(Direction direction) {
	String typeName = getEntityTypeName();
	String gql = GraphQueryBuilder.matchEdgeByType(typeName, direction);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseEdge> entityClass = this.getClass();
	  Object res = handleResult(resultSet, List.class, entityClass);
	  return (List<T>) res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 根据边的属性查询符合条件的边
   * @param srcVertex 起点
   * @param direction 边的方向
   * @param dstVertex 终点
   * @return 符合条件的边列表
   */
  public <I extends GraphBaseEdge, T extends GraphBaseVertex> List<I> queryByProperty(
		  T srcVertex, Direction direction, T dstVertex) {
	String typeName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = GraphQueryBuilder.matchEdgeWithEdgeProperty(srcVertex, dstVertex,
			typeName, entityProperties, direction);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseEdge> entityClass = this.getClass();
	  Object res = handleResult(resultSet, List.class, entityClass);
	  return (List<I>) res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 根据起始点查找边
   * @return 指定起始点的边
   */
  public <I extends GraphBaseEdge> I queryEdgeProperty() {
	String typeName = getEntityTypeName();
	Object srcId = getSrcId();
	Object dstId = getDstId();
	String gql = GraphQueryBuilder.fetchEdgeProperty(typeName, srcId, dstId);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseEdge> entityClass = this.getClass();
	  return (I) handleResult(resultSet, entityClass, null);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 统计指定类型的边的个数
   * @return 边的个数
   */
  public Integer queryEdgeCountByType() {
	String tagName = getEntityTypeName();
	String gql = lookupEdgeCount(tagName);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (Integer) handleResult(resultSet, Integer.class, null);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
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
	}
	else {
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
	  if (value != null) {
		properties.put(field.getName(), value);
	  }
	}
	return properties;
  }

  /**
   * 获取边的起点id
   */
  private Object getSrcId() {
	Object id = null;
	Class<? extends GraphBaseEdge> entityClass = this.getClass();
	Field[] declaredFields = entityClass.getDeclaredFields();
	for (Field field : declaredFields) {
	  Annotation[] annotations = field.getAnnotations();
	  for (Annotation annotation : annotations) {
		if (annotation instanceof SrcId) {
		  id = getValue(this, field);
		}
	  }
	}
	if (id == null) {
	  throw new RuntimeException(entityClass.getSimpleName()
			  + " does not have srcId.");
	}
	return id;
  }

  /**
   * 获取边的终点id
   */
  private Object getDstId() {
	Object id = null;
	Class<? extends GraphBaseEdge> entityClass = this.getClass();
	Field[] declaredFields = entityClass.getDeclaredFields();
	for (Field field : declaredFields) {
	  Annotation[] annotations = field.getAnnotations();
	  for (Annotation annotation : annotations) {
		if (annotation instanceof DstId) {
		  id = getValue(this, field);
		}
	  }
	}
	if (id == null) {
	  throw new RuntimeException(entityClass.getSimpleName() + " does not have dstId.");
	}
	return id;
  }
}
