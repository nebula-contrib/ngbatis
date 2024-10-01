package org.nebula.contrib.ngbatis.base;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.nebula.contrib.ngbatis.ArgsResolver;
import org.nebula.contrib.ngbatis.Env;
import org.nebula.contrib.ngbatis.ResultResolver;
import org.nebula.contrib.ngbatis.SessionDispatcher;
import org.nebula.contrib.ngbatis.annotations.base.EdgeType;
import org.nebula.contrib.ngbatis.annotations.base.GraphId;
import org.nebula.contrib.ngbatis.annotations.base.Tag;
import org.nebula.contrib.ngbatis.proxy.MapperProxy;
import org.nebula.contrib.ngbatis.session.LocalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.nebula.contrib.ngbatis.proxy.MapperProxy.executeWithParameter;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getNameByColumn;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getValue;

/**
 * 提供给实体调用的拓展方法。
 * @author xYLiuuuuuu
 * @since 2024/9/9 16:59
 */

public class GraphBaseExt {
  public static Env ENV;
  private static Logger log = LoggerFactory.getLogger(GraphBaseExt.class);

  public static ResultSet executeGql(String textTpl,Map<String,Object> m1,Map<String, Object> m2) {
	Session session = null;
	LocalSession localSession = null;
	ResultSet result = null;
	//从env中获取本地会话调度器
	SessionDispatcher dispatcher = ENV.getDispatcher();
	localSession = dispatcher.poll();
	//从env中获取space
	String currentSpace = ENV.getSpace();

	ArgsResolver argsResolver = ENV.getArgsResolver();

	String gql = ENV.getTextResolver().resolve(textTpl, m1);

	Map<String,Object> parasForDb = argsResolver.resolve(m2);

	String[] qlAndSpace = null;
	try {
	  //确保当前图空间正确
	  qlAndSpace = qlWithSpace(localSession, gql, currentSpace);
	  gql = qlAndSpace[1];
	  session = localSession.getSession();
	  result = session.executeWithParameter(gql,parasForDb);
	}
	catch (Exception e) {
	  throw new RuntimeException(e);
	}
	finally {
	  log.debug("\n\t- nGql：{}"
					  + "\n\t- params: {}"
					  + "\n\t- result：{}",
			  gql, m2, result);
	}
	return result;
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
	  // 获取 @EdgeType 注解
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
	  Object fieldValue = getValue(v2,field);
	  if (fieldValue == null) {
		continue;
	  }
	  // 如果是基本类型且是初始值，跳过
	  if (field.getType().isPrimitive()) {
		if (isPrimitiveDefaultValue(field.getType(), fieldValue)) {
		  continue; // 跳过初始值
		}
	  }
	  // 处理带有 @GraphId 注解的字段
	  if (field.isAnnotationPresent(GraphId.class)) {
		result.put("id", fieldValue);
	  }
	  else {
		result.put(getNameByColumn(field), fieldValue);
	  }
	}
	return result;
  }

  /**
   * 判断实体属性是否为基本类型且是默认值
   * @param type 实体类型
   * @param value 属性值
   * @return 判断结果
   */
  public static boolean isPrimitiveDefaultValue(Class<?> type, Object value) {
	if (type == int.class && (int) value == 0) {
	  return true;
	}
	if (type == long.class && (long) value == 0L) {
	  return true;
	}
	if (type == float.class && (float) value == 0.0f) {
	  return true;
	}
	if (type == double.class && (double) value == 0.0d) {
	  return true;
	}
	if (type == byte.class && (byte) value == 0) {
	  return true;
	}
	if (type == short.class && (short) value == 0) {
	  return true;
	}
	if (type == char.class && (char) value == '\u0000') { // 默认字符是空字符
	  return true;
	}
	return false;
  }

  /**
   * 将多个edgeClass拼接成edge子句
   * @param sep 分隔符
   * @param edgeClass 边类型
   * @return edge子句
   */
  public static String getEdgeTypes(String sep, Class<?>... edgeClass) {
	if (edgeClass == null) {
	  return "";
	}
	StringBuilder edgeTypeBuilder = new StringBuilder();
	for (Class<?> edgeType : edgeClass) {
	  String type = GraphBaseExt.getEdgeType(edgeType);
	  edgeTypeBuilder.append(type).append(sep);
	}
	if (edgeTypeBuilder.length() > 0) {
	  edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
	}
	return edgeTypeBuilder.toString();
  }
}
