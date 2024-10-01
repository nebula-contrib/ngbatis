package org.nebula.contrib.ngbatis.base;

import java.util.Objects;
import org.nebula.contrib.ngbatis.enums.Direction;
import org.nebula.contrib.ngbatis.enums.IdType;

/**
 * 生成实体直查所需的beetle模块语句
 * @author xYLiuuuuuu
 * @since 2024/8/29 16:38
 */

public class TextTplBuilder {

  public static String matchVertexId() {
    return "    MATCH (v:${ tag })\n"
      + "       WHERE 1 == 1\n"
      + "    @if ( isNotEmpty(properties) ) {\n"
      + "       @for ( prop in properties ) {\n"
      + "         @if ( prop.key == \"id\" ) {\n"
      + "             AND id(v) == $id\n"
      + "         @} else {\n"
      + "             AND v.${ tag }.${ prop.key } == $${ prop.key }\n"
      + "         @}\n"
      + "       @}\n"
      + "    @}\n"
      + "    RETURN id(v)";
  }


  public static String matchVertexByTag() {
    return "MATCH (v:${ tag }) RETURN v";
  }

  public static String fetchVertexById(IdType vertexIdType) {
    if (vertexIdType == IdType.STRING) {
      return "FETCH PROP ON ${tag} \"${id}\" YIELD vertex AS v";
    }
    return "FETCH PROP ON ${tag} ${id} YIELD vertex AS v";
  }

  public static String matchVertexSelective() {
    return "    MATCH (v:${ tag })\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v) == $id\n"
         + "          @} else {\n"
         + "             AND v.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN v";
  }

  public static String matchIncomingVertex(String edgeType) {
    String edgeClause = (edgeType != null && !edgeType.isEmpty()) ? "[e:${edgeTypes}]" : "[e]";
    return "    MATCH (v1)<-" + edgeClause + "-(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN v2";
  }

  public static String matchOutgoingVertex(String edgeType) {
    String edgeClause = (edgeType != null && !edgeType.isEmpty()) ? "[e:${edgeTypes}]" : "[e]";
    return "    MATCH (v1)-" + edgeClause + "->(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN v2";
  }

  public static String matchAllAdjacentVertex(String edgeType) {
    String edgeClause = (edgeType != null && !edgeType.isEmpty()) ? "[e:${edgeTypes}]" : "[e]";
    return "    MATCH (v1)-" + edgeClause + "-(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN DISTINCT v2";
  }

  public static String goAdjacentVertexWithSteps(String edgeType,IdType vertexIdType) {
    String edgeClause = (edgeType != null && !edgeType.isEmpty()) ? "${edgeTypes}" : "*";
    if (vertexIdType == IdType.STRING) {
      return "GO ${m} TO ${n} STEPS FROM \"${id}\" OVER " + edgeClause + "\n"
         + "        YIELD dst(edge) AS destination;";
    }
    return "GO ${m} TO ${n} STEPS FROM ${id} OVER " + edgeClause + "\n"
         + "        YIELD dst(edge) AS destination;";
  }

  public static String goAllEdgesFromVertex(String edgeType,IdType vertexIdType) {
    String edgeClause = (edgeType != null && !edgeType.isEmpty()) ? "${edgeTypes}" : "*";
    if (vertexIdType == IdType.STRING) {
      return "GO FROM \"${id}\" OVER " + edgeClause + " ${direction} \n"
         + "        YIELD edge AS e;";
    }
    return "GO FROM ${id} OVER " + edgeClause + " ${direction} \n"
         + "        YIELD edge AS e;";
  }

  public static String matchPath(Direction direction) {
    return "    MATCH p=(v1:${ tag })" + getMatchDirection(direction,"[e]") + "(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN p";
  }

  public static String matchAllShortestPaths(Integer maxHop, Direction direction) {
    String edge = String.format("[e*%s]", generateEdgeLengthPattern(null, maxHop));
    return "    MATCH p=allShortestPaths((v1:${srcTag})"
         + getMatchDirection(direction,edge) + "(v2:${dstTag}))\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(srcProperties) ) {\n"
         + "       @for ( prop in srcProperties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ srcTag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    @if ( isNotEmpty(dstProperties) ) {\n"
         + "       @for ( prop in dstProperties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v2) == $id\n"
         + "          @} else {\n"
         + "             AND v2.${ dstTag }.${ prop.key } == $v2_${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN p";
  }

  public static String matchShortestPaths(Integer maxHop,Direction direction) {
    String edge = String.format("[e*%s]", generateEdgeLengthPattern(null, maxHop));
    return "    MATCH p=shortestPath((v1:${srcTag})"
         + getMatchDirection(direction,edge) + "(v2:${dstTag}))\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(srcProperties) ) {\n"
         + "       @for ( prop in srcProperties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ srcTag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    @if ( isNotEmpty(dstProperties) ) {\n"
         + "       @for ( prop in dstProperties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v2) == $id\n"
         + "          @} else {\n"
         + "             AND v2.${ dstTag }.${ prop.key } == $v2_${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN p";
  }


  public static String matchFixedLengthPath(Integer maxHop, Direction direction, String edgeType) {
    String edgeClause = String.format("[e%s*%s]",
         (edgeType != null && !edgeType.isEmpty()) ? ":${edgeTypes}" : "",
         generateEdgeLengthPattern(null, maxHop));
    return "    MATCH p=(v1:${tag})" + getMatchDirection(direction,edgeClause) + "(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN DISTINCT p";
  }

  public static String matchVariableLengthPath(Integer minHop,Integer maxHop,
            Direction direction, String edgeType) {
    String edgeClause = String.format("[e%s*%s]",
         (edgeType != null && !edgeType.isEmpty()) ? ":${edgeTypes}" : "",
         generateEdgeLengthPattern(minHop, maxHop));
    return "    MATCH p=(v1:${tag})" + getMatchDirection(direction,edgeClause) + "(v2)\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(properties) ) {\n"
         + "       @for ( prop in properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $id\n"
         + "          @} else {\n"
         + "             AND v1.${ tag }.${ prop.key } == $${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN DISTINCT p";
  }

  public static String matchEdgeByType(Direction direction) {
    return "    MATCH ()" + getMatchDirection(direction,"[e:${edgeType}]") + "()\n"
         + "    RETURN e";
  }

  public static String matchEdgeWithEdgeProperty(Direction direction) {
    String v1Tag = "     @if ( isNotEmpty(v1Tag) ) {\n"
         + "              :${v1Tag}\n"
         + "             @}\n";
    String v2Tag = "     @if ( isNotEmpty(v2Tag) ) {\n"
         + "              :${v2Tag}\n"
         + "             @}\n";
    return "    MATCH (v1" + v1Tag + ")"
         + getMatchDirection(direction,"[e:${edgeType}]") + "(v2" + v2Tag + ")\n"
         + "       WHERE 1 == 1\n"
         + "    @if ( isNotEmpty(v1Properties) ) {\n"
         + "       @for ( prop in v1Properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v1) == $v1_id\n"
         + "          @} else {\n"
         + "             AND v1.${ v1Tag }.${ prop.key } == $v1_${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    @if ( isNotEmpty(v2Properties) ) {\n"
         + "       @for ( prop in v2Properties ) {\n"
         + "          @if ( prop.key == \"id\" ) {\n"
         + "             AND id(v2) == $v2_id\n"
         + "          @} else {\n"
         + "             AND v2.${ v2Tag }.${ prop.key } == $v2_${ prop.key }\n"
         + "          @}\n"
         + "       @}\n"
         + "    @}\n"
         + "    @if ( isNotEmpty(edgeProperties) ) {\n"
         + "       @for ( prop in edgeProperties ) {\n"
         + "             AND e.${ prop.key } == $${ prop.key }\n"
         + "       @}\n"
         + "    @}\n"
         + "    RETURN e";
  }

  public static String fetchEdgeProperty(Object srcIdType,Object dstIdType) {
    if (srcIdType == String.class && dstIdType == String.class) {
      return "FETCH PROP ON ${edgeType} \"${srcId}\" -> \"${dstId}\" YIELD edge AS e;";
    }
    return "FETCH PROP ON ${edgeType} ${srcId} -> ${dstId} YIELD edge AS e;";
  }

  public static String lookupVertexCount() {
    return "LOOKUP ON ${tag} YIELD id(vertex) | YIELD COUNT(*) AS Number";
  }

  public static String lookupEdgeCount() {
    return "LOOKUP ON ${tag} YIELD edge AS e | YIELD COUNT(*) AS Number";
  }

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

  private static String getMatchDirection(Direction direction,String edge) {
    String dir = "";
    if (Objects.equals(direction.getSymbol(), Direction.REVERSELY.getSymbol())) {
      dir = "<-" + edge + "-";
    } else if (Objects.equals(direction.getSymbol(), Direction.NULL.getSymbol())) {
      dir = "-" + edge + "->";
    } else {
      dir = "-" + edge + "-";
    }
    return dir;
  }
}
