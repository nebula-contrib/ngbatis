package org.nebula.contrib.ngbatis.base;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.nebula.contrib.ngbatis.Env;
import org.nebula.contrib.ngbatis.ResultResolver;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.annotations.base.EdgeType;
import org.nebula.contrib.ngbatis.annotations.base.GraphId;
import org.nebula.contrib.ngbatis.annotations.base.Tag;
import org.nebula.contrib.ngbatis.session.LocalSession;

/**
 * 提供给实体调用的拓展方法。
 * @author xYLiuuuuuu
 * @since 2024/9/9 16:59
 */

public class GraphBaseExt {
  public static Env ENV;

  /**
   * 执行gql
   * @param gql 查询语句
   * @return 结果集ResultSet
   */
  public static ResultSet executeGQL(String gql) {
	Session session = null;
	LocalSession localSession = null;
	ResultSet result = null;
	//从env中获取本地会话调度器
	SessionDispatcher dispatcher = ENV.getDispatcher();
	localSession = dispatcher.poll();
	//从env中获取space
	String currentSpace = ENV.getSpace();

	String[] qlAndSpace = null;
	try {
	  //确保当前图空间正确
	  qlAndSpace = qlWithSpace(localSession, gql, currentSpace);
	  gql = qlAndSpace[1];
	  session = localSession.getSession();
	  //查询结果
	  result = session.execute(gql);
	  return result;
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
  }

  /**
   * 处理查询结果
   * @param resultSet 结果集ResultSet
   * @param returnType 返回值类型
   * @param resultType 一般是泛型类型
   * @return 处理结果
   */
  public static Object handleResult(ResultSet resultSet, Class returnType, Class resultType) {
	ResultResolver resultResolver = ENV.getResultResolver();
	Object resolve = resultResolver.resolve(resultSet, returnType, resultType);
	return resolve;
  }

  private static String[] qlWithSpace(LocalSession localSession, String gql, String currentSpace)
		  throws IOErrorException, BindSpaceFailedException {
	String[] qlAndSpace = new String[2];
	gql = gql.trim();
	String sessionSpace = localSession.getCurrentSpace();
	boolean sameSpace = Objects.equals(sessionSpace, currentSpace);
	if (!sameSpace && currentSpace != null) {
	  qlAndSpace[0] = currentSpace;
	  Session session = localSession.getSession();
	  ResultSet execute = session.execute(String.format("USE `%s`", currentSpace));
	  if (!execute.isSucceeded()) {
		throw new BindSpaceFailedException(
				String.format(" %s \"%s\"", execute.getErrorMessage(), currentSpace)
		);
	  }
	}
	qlAndSpace[1] = String.format("\n\t\t%s", gql);
	return qlAndSpace;
  }

  /**
   * 得到某个边实体的type
   * @param edgeClass 实体类
   * @return 边的type
   */
  public static String getEdgeType(Class edgeClass) {
	if (edgeClass.isAnnotationPresent(EdgeType.class)) {
	  // 获取 @Tag 注解
	  EdgeType anno = (EdgeType) edgeClass.getAnnotation(EdgeType.class);
	  // 获取 name 属性值
	  return anno.name();
	}
	else {
	  throw new RuntimeException("Entity " + edgeClass.getName()
			  + " does not have @Tag annotation.");
	}
  }


  /**
   * 得到某个点实体的type
   * @param v2 点实体
   * @return 点的Tag
   */
  public static <T extends GraphBaseVertex> String getV2Tag(T v2) {
	Class<? extends GraphBaseVertex> v2Class = v2.getClass();
	if (v2Class.isAnnotationPresent(Tag.class)) {
	  Tag tagAnnotation = v2Class.getAnnotation(Tag.class);
	  return tagAnnotation.name();
	}
	else {
	  throw new RuntimeException("Entity " + v2Class.getName() + " does not have @Tag annotation.");
	}
  }

  /**
   * 得到某个点实体的属性map
   * @param v2 点实体
   * @return 点的属性map
   */
  public static <T extends GraphBaseVertex> Map<String, Object> getV2Property(T v2) {
	Class<? extends GraphBaseVertex> v2Class = v2.getClass();
	Map<String, Object> result = new HashMap<String, Object>();
	Field[] fields = v2Class.getDeclaredFields();
	for (Field field : fields) {
	  if (!field.isAnnotationPresent(GraphId.class)) {
		try {
		  field.setAccessible(true);
		  String fieldName = field.getName();
		  Object fieldValue = field.get(v2);
		  result.put(fieldName, fieldValue);
		}
		catch (IllegalAccessException e) {
		  throw new RuntimeException("Unable to access field: " + field.getName(), e);
		}
	  }
	}
	return result;
  }
}
