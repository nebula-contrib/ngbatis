package org.nebula.contrib.ngbatis.base;

import java.util.Map;
import java.util.Objects;

import org.nebula.contrib.ngbatis.enums.Direction;

import static org.nebula.contrib.ngbatis.base.GraphBaseExt.getV2Property;
import static org.nebula.contrib.ngbatis.base.GraphBaseExt.getV2Tag;


/**
 * 生成实体直查所需的gql语句
 * @author xYLiuuuuuu
 * @since 2024/8/29 16:38
 */

public class GraphQueryBuilder {

	/**
	 * 匹配带有条件的点的id
	 * @param tagName 点的Tag
	 * @param properties 点的属性
	 * @return gql查询语句
	 */
	public static String lookupVertexId(String tagName, Map<String, Object> properties){
		StringBuilder whereClause = new StringBuilder();
		if (properties != null && !properties.isEmpty()) {
			whereClause.append(getWhereCondition(properties,tagName));
		}
		String whereClauseStr = whereClause.length() > 0 ? "WHERE " + whereClause : "";

		return String.format("LOOKUP ON %s %s YIELD id(vertex)",tagName,whereClauseStr);
	}

	/**
	 * 将属性条件组合成where子句
	 * @param properties 属性，目前还不支持布尔,日期与时间，地理空间类型
	 * @param tagName 点的Tag
	 * @return where条件子句
	 */
	private static String getWhereCondition(Map<String, Object> properties,String tagName) {
		StringBuilder whereClause = new StringBuilder();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			if (whereClause.length() > 0) {
				whereClause.append(" AND ");
			}

			// 属性值类型是数值类型 int float double
			if (entry.getValue() instanceof Integer || entry.getValue() instanceof Double) {
				whereClause.append(String.format("%s.%s == %s", tagName,entry.getKey(),entry.getValue()));
			}
			// 属性值类型是字符串类型 String
			else if (entry.getValue() instanceof String) {
				whereClause.append(String.format("%s.%s == \"%s\"", tagName,entry.getKey(),entry.getValue()));
			}
			// TODO：支持布尔,日期与时间,地理空间类型
			else{
				throw new RuntimeException("Unsupported type: " + entry.getValue().getClass());
			}
		}
		return whereClause.toString();
	}

	/**
	 * 匹配指定Tag的点
	 * @param tagName 点的Tag
	 * @return gql查询语句
	 */
	public static String matchVertexByTag(String tagName) {
		return "MATCH (v:" + tagName + ") RETURN v";
	}

	/**
	 * 匹配指定id的点
	 * @param vertexId 点的id
	 * @param tagName 点的类型
	 * @return gql查询语句
	 */
	public static String fetchVertexById(Object vertexId,String tagName){
		Object vertex = getId(vertexId);
		return String.format("FETCH PROP ON %s %s YIELD vertex AS v",tagName,vertex);
	}

	/**
	 * 根据id类型判断是否加引号
	 * @param vertexId 点id
	 * @return id
	 */
	private static Object getId(Object vertexId){
		if(vertexId instanceof String){
			return String.format("\"%s\"", vertexId);
		}
		else if(vertexId instanceof Integer){
			return Long.valueOf(vertexId.toString());
		}
		else{
			throw new RuntimeException("Unsupported idType: " + vertexId.getClass());
		}
	}

	/**
	 * 匹配符合条件的点
	 * @param tagName 点的Tag
	 * @param properties 点的属性
	 * @return gql查询语句
	 */
	public static String matchVertexSelective(String tagName, Map<String, Object> properties) {
		String vertexCondition = (properties == null || properties.isEmpty())
				? ""
				: propertiesToCondition(properties);

		String vertexPattern = vertexCondition.isEmpty()
				? String.format("(%s:%s)", "v", tagName)
				: String.format("(%s:%s{%s})", "v", tagName, vertexCondition);

		return String.format("MATCH %s RETURN v",vertexPattern);
	}

	/**
	 * 将属性组合成条件子句
	 * @param properties 属性对
	 * @return key1:value2,key2:value2,key3:value3的形式
	 */
	private static String propertiesToCondition(Map<String, Object> properties) {
		StringBuilder conditionBuilder = new StringBuilder();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			if (entry.getValue() != null) {
				if (conditionBuilder.length() > 0) {
					conditionBuilder.append(", ");
				}
				conditionBuilder.append(createPropertyCondition(entry.getKey(), entry.getValue()));
			}
		}
		return conditionBuilder.toString();
	}

	/**
	 * 根据属性value的类型判断是否加引号
	 * @param key 属性键
	 * @param value 属性值
	 * @return key:value 或者 key:"value"
	 */
	private static String createPropertyCondition(String key, Object value) {
		String formattedValue;
		if (value instanceof String) {
			formattedValue = "\"" + value + "\"";
		} else if (value instanceof Number || value instanceof Boolean) {
			formattedValue = value.toString();
		} else {
			formattedValue = "\"" + value.toString() + "\"";
		}
		return String.format("%s: %s", key, formattedValue);
	}

	/**
	 * 匹配入边方向的起点（邻点）
	 * @param tagName 点的Tag
	 * @param vertexId 点的Id
	 * @param properties 点的属性
	 * @param edgeClass 边的类型
	 * @return gql查询语句
	 */
	public static String matchIncomingVertex(String tagName,Object vertexId,Map<String, Object> properties,Class<?>... edgeClass){
		String curVertexCondition = (properties == null || properties.isEmpty()) ? "" : propertiesToCondition(properties);
		String curVertex = curVertexCondition.isEmpty() ? tagName : String.format("%s{%s}", tagName, curVertexCondition);

		String whereId = vertexId==null ? "" : String.format("WHERE id(v1) == %s",getId(vertexId));

		String edge = "";
		if(edgeClass.length > 0){
			StringBuilder edgeTypeBuilder = new StringBuilder();
			for (Class<?> edgeType : edgeClass) {
				String type = GraphBaseExt.getEdgeType(edgeType);
				edgeTypeBuilder.append(type).append("|");
			}
			if (edgeTypeBuilder.length() > 0) {
				edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
			}
			edge = String.format("[e:%s]",edgeTypeBuilder);
		}else{
			edge = "[e]";
		}

		return String.format("MATCH (v1:%s)<-%s-(v2) %s RETURN v2", curVertex,edge,whereId);
	}

	/**
	 * 匹配出边方向的终点（邻点）
	 * @param tagName 点的Tag
	 * @param vertexId 点的Id
	 * @param properties 点的属性
	 * @param edgeClass 边的类型
	 * @return gql查询语句
	 */
	public static String matchOutgoingVertex(String tagName,Object vertexId,Map<String, Object> properties,Class<?>... edgeClass){
		String curVertexCondition = (properties == null || properties.isEmpty()) ? "" : propertiesToCondition(properties);
		String curVertex = curVertexCondition.isEmpty() ? tagName : String.format("%s{%s}", tagName, curVertexCondition);

		String whereId = vertexId==null ? "" : String.format("WHERE id(v1) == %s",getId(vertexId));

		String edge = "";
		if(edgeClass.length > 0){
			StringBuilder edgeTypeBuilder = new StringBuilder();
			for (Class<?> edgeType : edgeClass) {
				String type = GraphBaseExt.getEdgeType(edgeType);
				edgeTypeBuilder.append(type).append("|");
			}
			if (edgeTypeBuilder.length() > 0) {
				edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
			}
			edge = String.format("[e:%s]",edgeTypeBuilder);
		}else{
			edge = "[e]";
		}

		return String.format("MATCH (v1:%s)-%s->(v2) %s RETURN v2", curVertex,edge,whereId);
	}

	/**
	 * 匹配所有邻点
	 * @param tagName 点的Tag
	 * @param vertexId 点的Id
	 * @param properties 点的属性
	 * @param edgeClass 边的类型
	 * @return gql查询语句
	 */
	public static String matchAllAdjacentVertex(String tagName,Object vertexId,Map<String, Object> properties,Class<?>... edgeClass) {
		//构造当前点的属性条件
		String curVertexCondition = (properties == null || properties.isEmpty()) ? "" : propertiesToCondition(properties);
		String curVertex = curVertexCondition.isEmpty() ? tagName : String.format("%s{%s}", tagName, curVertexCondition);

		//构造当前点的id条件（若有）
		String whereId = vertexId==null ? "" : String.format("WHERE id(v1) == %s",getId(vertexId));

		//构造和相邻的点之间的边的类型条件
		String edge = "";
		if(edgeClass.length > 0){
			StringBuilder edgeTypeBuilder = new StringBuilder();
			for (Class<?> edgeType : edgeClass) {
				String type = GraphBaseExt.getEdgeType(edgeType);
				edgeTypeBuilder.append(type).append("|");
			}
			if (edgeTypeBuilder.length() > 0) {
				edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
			}
			edge = String.format("[e:%s]",edgeTypeBuilder);
		}else{
			edge = "[e]";
		}

		return String.format("MATCH (v1:%s)-%s-(v2) %s RETURN v2", curVertex,edge,whereId);
	}

	/**
	 * 匹配指定跳数内的点
	 * @param vertexId 点的id
	 * @param m 最大跳数
	 * @param n 最小跳数
	 * @param edgeClass 边的类型
	 * @return gql查询语句
	 */
	public static String goAdjacentVertexWithSteps(Object vertexId,int m,int n,Class<?>... edgeClass){
		if (m < 0 || n < 0 || m > n) {
			throw new IllegalArgumentException("跳数范围不正确");
		}

		String edgeType = "";
		if(edgeClass == null || edgeClass.length == 0){
			edgeType = "*";
		}
		else{
			StringBuilder edgeTypeBuilder = new StringBuilder();
			for (Class<?> e : edgeClass) {
				String type = GraphBaseExt.getEdgeType(e);
				edgeTypeBuilder.append(type).append(",");
			}
			if (edgeTypeBuilder.length() > 0) {
				edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
			}
			edgeType = edgeTypeBuilder.toString();
		}

		String stepClause = (m == n)
				? String.format("%d STEPS", n)
				: String.format("%d TO %d STEPS", m, n);

		return String.format("GO %s FROM %s OVER %s YIELD dst(edge) AS destination", stepClause, getId(vertexId), edgeType);
	}

	/**
	 * 查询指定点的所有邻边
	 * @param srcVetexId 起点id
	 * @param direction 边的方向
	 * @return gql查询语句
	 */
	public static String goAllEdgesFromVertex(Object srcVetexId, Direction direction){
		Object srcId = getId(srcVetexId);
		return String.format("GO FROM %s OVER * %s YIELD edge AS e",srcId,direction.getSymbol());
	}

	/**
	 * 查询指定起点的路径
	 * @param tagName 点的tag
	 * @param srcProperties 点的属性
	 * @param direction 边的方向
	 * @return gql查询语句
	 */
	public static String matchPath(String tagName,Map<String, Object> srcProperties,Direction direction){
		String dir = getMatchDirection(direction);

		String srcCondition = (srcProperties == null || srcProperties.isEmpty()) ? "" : propertiesToCondition(srcProperties);
		String srcPattern = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex", tagName)
				: String.format("(%s:%s{%s})", "srcVertex", tagName, srcCondition);

		return String.format("MATCH p=%s%s(v2) RETURN p",srcPattern, dir);
	}

	/**
	 * 查询指定起点的所有最短路径
	 * @param srcTag 起点Tag
	 * @param srcPropertyMap 起点属性
	 * @param maxHop 最大步数
	 * @param direction 方向
	 * @param v2 终点
	 * @return gql查询语句
	 * @param <T> 终点实体
	 */
	public static <T extends GraphBaseVertex> String matchAllShortestPaths(String srcTag, Map<String, Object> srcPropertyMap,
			Integer maxHop, Direction direction, T v2){
		if (maxHop != null && maxHop < 0) {
			throw new RuntimeException("maxHop must be greater than or equal to zero");
		}
		String dir = getMatchDirection(direction);

		String srcCondition = (srcPropertyMap == null || srcPropertyMap.isEmpty()) ? "" : propertiesToCondition(srcPropertyMap);
		String srcVertex = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex", srcTag)
				: String.format("(%s:%s{%s})", "srcVertex", srcTag, srcCondition);


		String dstTag = getV2Tag(v2);
		String dstCondition = propertiesToCondition(getV2Property(v2));
		String dstVertex = dstCondition.isEmpty()
				? String.format("(%s:%s)", "dstVertex",dstTag)
				: String.format("(%s:%s{%s})", "dstVertex", dstTag, dstCondition);

		String edge = String.format("[e*%s]", generateEdgeLengthPattern(null, maxHop));

		return String.format("MATCH p = allShortestPaths(%s%s%s) RETURN p", srcVertex, getEdgePattern(edge, dir),dstVertex);
	}

	/**
	 * 查询指定起点的最短路径
	 * @param srcTag 起点Tag
	 * @param srcPropertyMap 起点属性
	 * @param maxHop 最大步数
	 * @param direction 方向
	 * @param v2 终点
	 * @return gql查询语句
	 * @param <T> 终点实体
	 */
	public static <T extends GraphBaseVertex> String matchShortestPaths(String srcTag, Map<String, Object> srcPropertyMap,
			Integer maxHop, Direction direction,
			T v2){

		if (maxHop != null && maxHop < 0) {
			throw new RuntimeException("maxHop must be greater than or equal to zero");
		}

		String dir = getMatchDirection(direction);

		String srcCondition = (srcPropertyMap == null || srcPropertyMap.isEmpty()) ? "" : propertiesToCondition(srcPropertyMap);
		String srcVertex = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex", srcTag)
				: String.format("(%s:%s{%s})", "srcVertex", srcTag, srcCondition);


		String dstTag = getV2Tag(v2);
		String dstCondition = propertiesToCondition(getV2Property(v2));
		String dstVertex = dstCondition.isEmpty()
				? String.format("(%s:%s)", "dstVertex",dstTag)
				: String.format("(%s:%s{%s})", "dstVertex", dstTag, dstCondition);

		String edge = String.format("[e*%s]", generateEdgeLengthPattern(null, maxHop));

		return String.format("MATCH p = shortestPath(%s%s%s) RETURN p", srcVertex, getEdgePattern(edge, dir),dstVertex);
	}


	/**
	 * 查询指定起点的定长路径
	 * @param srcTag 起点tag
	 * @param srcPropertyMap 起点属性
	 * @param maxHop 最大步数
	 * @param direction 边方向
	 * @param edgeClass 边类型
	 * @return gql查询语句
	 */
	public static String matchFixedLengthPath(String srcTag, Map<String, Object> srcPropertyMap,
			Integer maxHop,Direction direction,Class<?>... edgeClass){
		if (maxHop != null && maxHop < 0) {
			throw new RuntimeException("maxHop must be greater than or equal to zero");
		}

		String dir = getMatchDirection(direction);

		String srcCondition = (srcPropertyMap == null || srcPropertyMap.isEmpty()) ? "" : propertiesToCondition(srcPropertyMap);
		String srcVertex = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex", srcTag)
				: String.format("(%s:%s{%s})", "srcVertex", srcTag, srcCondition);

		StringBuilder edgeTypeBuilder = new StringBuilder();
		for (Class<?> edgeType : edgeClass) {
			String type = GraphBaseExt.getEdgeType(edgeType);
			edgeTypeBuilder.append(type).append("|");
		}
		if (edgeTypeBuilder.length() > 0) {
			edgeTypeBuilder.setLength(edgeTypeBuilder.length() - 1);
		}
		String edge = String.format("[e:%s*%d]", edgeTypeBuilder,maxHop);

		return String.format("MATCH p = %s%s(v2) RETURN DISTINCT p",srcVertex,getEdgePattern(edge, dir));
	}


	/**
	 * 查询指定起点的变长路径
	 * @param srcTag 起点tag
	 * @param srcPropertyMap 起点属性
	 * @param minHop 最小步数
	 * @param maxHop 最大步数
	 * @param direction 边方向
	 * @param edgeClass 边类型
	 * @return gql查询语句
	 */
	public static String matchVariableLengthPath(String srcTag, Map<String, Object> srcPropertyMap,
			Integer minHop,Integer maxHop,Direction direction,Class<?> edgeClass){
		if (maxHop != null && maxHop < 0) {
			throw new RuntimeException("maxHop must be greater than or equal to zero");
		}
		if (minHop != null && minHop < 0) {
			throw new RuntimeException("minHop must be greater than or equal to zero");
		}

		String dir = getMatchDirection(direction);

		String srcCondition = (srcPropertyMap == null || srcPropertyMap.isEmpty()) ? "" : propertiesToCondition(srcPropertyMap);
		String srcVertex = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex", srcTag)
				: String.format("(%s:%s{%s})", "srcVertex", srcTag, srcCondition);

		String edgeType = (edgeClass == null) ? "" : GraphBaseExt.getEdgeType(edgeClass);
		String edge = String.format("[e:%s*%s]", edgeType,generateEdgeLengthPattern(minHop, maxHop));

		return String.format("MATCH p = %s%s(v2) RETURN DISTINCT p",srcVertex,getEdgePattern(edge, dir));
	}

	/**
	 * 查找指定type和方向的边
	 * @param type 边类型
	 * @param direction 边方向
	 * @return gql查询语句
	 */
	public static String matchEdgeByType(String type,Direction direction) {
		String dir = getMatchDirection(direction);
		return String.format("MATCH ()%s() RETURN e", getEdgePattern("[e:"+type+"]",dir));
	}

	/**
	 * 查找符合条件的边
	 * @param srcVertex 起点
	 * @param dstVertex 终点
	 * @param type 边的类型
	 * @param propertyMap 边的属性
	 * @param direction 方向
	 * @return gql查询语句
	 * @param <T> 点实体
	 */
	public static <T extends GraphBaseVertex> String matchEdgeWithEdgeProperty(T srcVertex,T dstVertex,String type, Map<String, Object> propertyMap,Direction direction) {
		String dir = getMatchDirection(direction);

		String edgeCondition = (propertyMap == null || propertyMap.isEmpty())
				? "": propertiesToCondition(propertyMap);
		String edge = edgeCondition.isEmpty()
				? String.format("[e:%s]", type)
				: String.format("[e:%s{%s}]", type, edgeCondition);

		String srcTag = getV2Tag(srcVertex);
		String srcCondition = propertiesToCondition(getV2Property(srcVertex));
		String src = srcCondition.isEmpty()
				? String.format("(%s:%s)", "srcVertex",srcTag)
				: String.format("(%s:%s{%s})", "srcVertex", srcTag, srcCondition);

		String dstTag = getV2Tag(dstVertex);
		String dstCondition = propertiesToCondition(getV2Property(dstVertex));
		String dst = dstCondition.isEmpty()
				? String.format("(%s:%s)", "dstVertex",dstTag)
				: String.format("(%s:%s{%s})", "dstVertex", dstTag, dstCondition);

		return String.format(
				"MATCH %s%s%s RETURN e",src, getEdgePattern(edge,dir),dst
		);
	}

	/**
	 * 将Direction转为箭头方向
	 * @param direction 方向
	 * @return 箭头
	 */
	private static String getMatchDirection(Direction direction) {
		String dir = "";
		if(Objects.equals(direction.getSymbol(), Direction.REVERSELY.getSymbol())){
			dir = "<--";
		}
		else if(Objects.equals(direction.getSymbol(),Direction.NULL.getSymbol())){
			dir = "-->";
		}
		else {
			dir = "--";
		}
		return dir;
	}

	/**
	 * 查找指定起始点的边
	 * @param type 边的类型
	 * @param srcVertexId 起点id
	 * @param dstVertexId 终点id
	 * @return gql查询语句
	 */
	public static String fetchEdgeProperty(String type,Object srcVertexId,Object dstVertexId){
		Object srcId = getId(srcVertexId);
		Object dstId = getId(dstVertexId);
		return String.format("FETCH PROP ON %s %s -> %s YIELD edge AS e;",type,srcId,dstId);
	}

	/**
	 * 匹配指定tag的点的数量
	 * @param tagName 点的tag
	 * @return gql查询语句
	 */
	public static String lookupVertexCount(String tagName){
		return String.format("LOOKUP ON %s YIELD id(vertex) | YIELD COUNT(*) AS Number",tagName);
	}

	/**
	 * 匹配指定type的边的数量
	 * @param typeName 边的type
	 * @return gql查询语句
	 */
	public static String lookupEdgeCount(String typeName){
		return String.format("LOOKUP ON %s YIELD edge AS e | YIELD COUNT(*) AS Number",typeName);
	}

	/**
	 * 将边和方向组合在一起
	 * @param edge 边，可能带有属性，可能带有type，可能带有hop
	 * @param direction 方向
	 * @return -[edge]->
	 */
	private static String getEdgePattern(String edge,String direction){
		String edgePattern = "-"+edge+"-";

		if(direction!=null&&direction.equals("<--")){
			edgePattern = "<"+edgePattern;
		}
		else if(direction!=null&&direction.equals("-->")){
			edgePattern = edgePattern+">";
		}
		return edgePattern;
	}

	/**
	 * 生成跳数组合
	 * @param minHop 最小跳数
	 * @param maxHop 最大跳数
	 * @return minHop..maxHop
	 */
	private static String generateEdgeLengthPattern(Integer minHop, Integer maxHop) {
		if (minHop == null && maxHop == null) {
			return "";
		} else if (minHop == null) {
			minHop = 1;  // 默认最小跳数为 1
		}
		if (maxHop == null) {
			return minHop + "..";  // 无限大
		} else {
			return minHop + ".." + maxHop;
		}
	}
}
