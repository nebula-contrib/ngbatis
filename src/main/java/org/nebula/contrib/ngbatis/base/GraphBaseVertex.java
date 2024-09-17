package org.nebula.contrib.ngbatis.base;

import static org.nebula.contrib.ngbatis.base.GraphBaseExt.executeGQL;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.handleResult;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.goAdjacentVertexWithSteps;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.goAllEdgesFromVertex;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.lookupVertexCount;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchAllAdjacentVertex;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchAllShortestPaths;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchFixedLengthPath;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchIncomingVertex;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchOutgoingVertex;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchPath;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchShortestPaths;
import static org.nebula.contrib.ngbatis.base.GraphQueryBuilder.matchVariableLengthPath;
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
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 点实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:02
 */

public abstract class GraphBaseVertex extends GraphBase {
  private Logger log = LoggerFactory.getLogger(GraphBaseVertex.class);

  /**
   * 查询点的id
   * @return 点的id
   */
  public <T> List<T> queryId() {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = GraphQueryBuilder.lookupVertexId(tagName, entityProperties);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  Class<?> returnIdType = getVertexIdType();
	  Object res = handleResult(resultSet, List.class, returnIdType);
	  if (res == null) {
		throw new RuntimeException("No matching vertex was found.");
	  }
	  return (List<T>) res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 根据点的id查询点
   * @return 符合条件的点实体
   */
  public <T extends GraphBaseVertex> T queryById() {
	String tagName = getEntityTypeName();
	Object id = getVertexId();
	String gql = GraphQueryBuilder.fetchVertexById(id, tagName);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseVertex> entityClass = this.getClass();
	  Object o = handleResult(resultSet, entityClass, null);
	  return (T) o;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 根据点的Tag查询点
   * @return 符合条件的点集合
   */
  public <T extends GraphBaseVertex> List<T> queryByTag() {
	String tagName = getEntityTypeName();
	String gql = GraphQueryBuilder.matchVertexByTag(tagName);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseVertex> entityClass = this.getClass();
	  Object res = handleResult(resultSet, List.class, entityClass);
	  return (List<T>) res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询点的属性
   * @return 符合条件的点集合
   */
  public <T extends GraphBaseVertex> List<T> queryByProperty() {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = GraphQueryBuilder.matchVertexSelective(tagName, entityProperties);
	log.info("nGQL:{}", gql);
	ResultSet resultSet = null;
	try {
	  resultSet = executeGQL(gql);
	  Class<? extends GraphBaseVertex> entityClass = this.getClass();
	  Object res = handleResult(resultSet, List.class, entityClass);
	  return (List<T>) res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询所有邻点
   * @param edgeClass 边类型
   * @return 邻点集合
   */
  public <I> List<NgVertex<I>> queryAllAdjacentVertex(Class<?>... edgeClass) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	Map<String, Object> idValue = getIdValue();
	String gql = "";
	if (!idValue.isEmpty()) { //设置了id
	  gql = matchAllAdjacentVertex(tagName, getVertexId(), entityProperties, edgeClass);
	}
	else {
	  gql = matchAllAdjacentVertex(tagName, null, entityProperties, edgeClass);
	}
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询入边方向的邻点
   * @param edgeClass 边类型
   */
  public <I> List<NgVertex<I>> queryIncomingAdjacentVertex(Class<?>... edgeClass) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	Map<String, Object> idValue = getIdValue();
	String gql = "";
	if (!idValue.isEmpty()) { //设置了id
	  gql = matchIncomingVertex(tagName, getVertexId(), entityProperties, edgeClass);
	}
	else {
	  gql = matchIncomingVertex(tagName, null, entityProperties, edgeClass);
	}
	log.info("nGQL:{}", gql);
	try {
	  //处理gql
	  ResultSet resultSet = executeGQL(gql);
	  //处理resultSet结果
	  return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询出边方向的邻点
   * @param edgeClass 边类型
   * @return 出边方向邻点集合
   */
  public <I> List<NgVertex<I>> queryOutgoingAdjacentVertex(Class<?>... edgeClass) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	Map<String, Object> idValue = getIdValue();
	String gql = "";
	if (!idValue.isEmpty()) { //设置了id
	  gql = matchOutgoingVertex(tagName, getVertexId(), entityProperties, edgeClass);
	}
	else {
	  gql = matchOutgoingVertex(tagName, null, entityProperties, edgeClass);
	}
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgVertex<I>>) handleResult(resultSet, List.class, NgVertex.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询一个点在指定跳数内的所有点
   * @param m 最大跳数
   * @param n 最小跳数
   * @param edgeClass 边类型
   * @return 符合条件的点集合
   */
  public <T> List<T> queryNeighborsWithHop(int m, int n, Class<?>... edgeClass) {
	String gql = goAdjacentVertexWithSteps(getVertexId(), m, n, edgeClass);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  Class<T> returnIdType = (Class<T>) getVertexIdType();
	  List res = (List) handleResult(resultSet, List.class, returnIdType);
	  if (res == null) {
		throw new RuntimeException("No matching vertex was found.");
	  }
	  return res;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询起始点关联的所有边
   * @param direction 边方向
   * @return 点关联的所有边
   */
  public <T> List<NgEdge<T>> queryConnectedEdges(Direction direction) {
	Object id = getVertexId();
	String gql = goAllEdgesFromVertex(id, direction);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgEdge<T>>) handleResult(resultSet, List.class, NgEdge.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询符合条件的路径
   * @param direction 边的方向
   * @return 符合条件的路径
   */
  public <T> List<NgPath<T>> queryPathFromVertex(Direction direction) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();

	String gql = matchPath(tagName, entityProperties, direction);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgPath<T>>) handleResult(resultSet, List.class, NgPath.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询起始点到目标点任意一条最短路径
   * @param maxHop 路径最大长度
   * @param v2 终点
   * @return 符合条件的任意一条最短路径
   */
  public <T extends GraphBaseVertex, I> NgPath<I> queryShortestPath(Integer maxHop,
		  Direction direction, T v2) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = matchShortestPaths(tagName, entityProperties, maxHop, direction, v2);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (NgPath<I>) handleResult(resultSet, NgPath.class, null);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询起始点到目标点所有最短路径
   * @param maxHop 路径最大长度
   * @param direction 边类型
   * @param v2 终点
   * @return 符合条件的所有最短路径
   */
  public <T extends GraphBaseVertex, I> List<NgPath<I>> queryAllShortestPaths(Integer maxHop,
		  Direction direction, T v2) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = matchAllShortestPaths(tagName, entityProperties, maxHop, direction, v2);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 查询定长路径
   * @param maxHop 最大跳数
   * @param direction 方向
   * @param edgeClass 边类型
   * @return 符合条件的路径集合
   */
  public <I> List<NgPath<I>> queryFixedLengthPath(Integer maxHop,
		  Direction direction, Class<?>... edgeClass) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = matchFixedLengthPath(tagName, entityProperties, maxHop, direction, edgeClass);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }


  /**
   * 查询变长路径
   * @param minHop 最小跳数
   * @param maxHop 最大跳数
   * @param direction 方向
   * @param edgeClass 边类型
   * @return 符合条件的路径集合
   */
  public <I> List<NgPath<I>> queryVariableLengthPath(Integer minHop, Integer maxHop,
		  Direction direction, Class<?> edgeClass) {
	String tagName = getEntityTypeName();
	Map<String, Object> entityProperties = getEntityProperties();
	String gql = matchVariableLengthPath(tagName, entityProperties,
			minHop, maxHop, direction, edgeClass);
	log.info("nGQL:{}", gql);
	try {
	  ResultSet resultSet = executeGQL(gql);
	  return (List<NgPath<I>>) handleResult(resultSet, List.class, NgPath.class);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 统计指定类型的点的个数
   * @return 点的个数
   */
  public Integer queryVertexCountByTag() {
	String tagName = getEntityTypeName();
	String gql = lookupVertexCount(tagName);
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
   * 获取点的Tag
   */
  @Override
  protected String getEntityTypeName() {
	Class<? extends GraphBaseVertex> entityClass = this.getClass();
	if (entityClass.isAnnotationPresent(Tag.class)) {
	  Tag tagAnnotation = entityClass.getAnnotation(Tag.class);
	  return tagAnnotation.name();
	}
	else {
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
	  if (!field.isAnnotationPresent(GraphId.class)) {
		Object value = getValue(this, field);
		if (value != null) {
		  properties.put(getNameByColumn(field), value);
		}
	  }
	}
	return properties;
  }

  /**
   * 获取点的id值
   * @return map形式id值
   */
  private Map<String, Object> getIdValue() {
	Map<String, Object> idValue = new HashMap<>();
	Class<? extends GraphBaseVertex> entityClass = this.getClass();
	Field[] declaredFields = entityClass.getDeclaredFields();
	for (Field field : declaredFields) {
	  if (field.isAnnotationPresent(GraphId.class)) {
		Object value = getValue(this, field);
		if (value != null) {
		  idValue.put(field.getName(), value);
		}
		break;
	  }
	}
	return idValue;
  }

  /**
   * 获取点的id值
   * @return id值
   */
  private Object getVertexId() {
	Object id = null;
	Class<? extends GraphBaseVertex> entityClass = this.getClass();
	Field[] declaredFields = entityClass.getDeclaredFields();
	for (Field field : declaredFields) {
	  Annotation[] annotations = field.getAnnotations();
	  for (Annotation annotation : annotations) {
		if (annotation instanceof GraphId) {
		  id = getValue(this, field);
		}
	  }
	}
	if (id == null) {
	  throw new RuntimeException(entityClass.getSimpleName() + " does not have id.");
	}
	return id;
  }

  /**
   * 获取点的id类型
   */
  private Class<?> getVertexIdType() {
	Class<? extends GraphBaseVertex> entityClass = this.getClass();
	Field[] declaredFields = entityClass.getDeclaredFields();
	for (Field field : declaredFields) {
	  Annotation[] annotations = field.getAnnotations();
	  for (Annotation annotation : annotations) {
		if (annotation instanceof GraphId) {
		  return field.getType();
		}
	  }
	}
	return null;
  }
}
