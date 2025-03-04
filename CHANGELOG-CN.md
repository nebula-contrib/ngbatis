
<!--
Copyright (c) 2024 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# 2.0.0-beta.1

## Bugfix

- fix: 修复 `ng.valueFmt( value )` 中 value 为 null 时，无法输出 null 进行占位的问题。

# 2.0.0-beta

## Bug修复

- fix: [#329](https://github.com/nebula-contrib/ngbatis/issues/329) 修正返回值类型并明确接口泛型。[#335](https://github.com/nebula-contrib/ngbatis/pull/335)
- fix: 移除 JDK8 的内部 API: ParameterizedTypeImpl

## 新特性

- feat: 实体直接搜索。 ([#319](https://github.com/nebula-contrib/ngbatis/pull/319), 来自：[@xYLiuuuuuu](https://github.com/n3A87))
  - 实体可以继承 `GraphBaseVertex` 或 `GraphBaseEdge` 来支持直接搜索。
    - GraphBaseVertex:

    API | 用法说明
    --|--
    queryIdsByProperties()                           | 查询特定Tag或者属性的点Id集合
    queryVertexById()                                | 查询特定点Id的单个点
    queryVertexByTag()                               | 查询特定Tag的点集合
    queryVertexByProperties()                        | 查询特定属性的点集合
    queryAllAdjacentVertex(Class<?>... edgeClass)    | 查询特定点的所有邻点集合，可指定一个或多个连接两点的边类型
    queryIncomingAdjacentVertex(Class<?>... edgeClass) | 查询特定点入边方向的邻点集合，可指定一个或多个连接两点的边类型
    queryOutgoingAdjacentVertex(Class<?>... edgeClass) | 查询特定点出边方向的邻点集合，可指定一个或多个连接两点的边类型
    queryNeighborIdsWithHopById(int m, int n, Class<?>... edgeClass) | 查询特定点指定跳数内的点Id集合，可指定一个或多个连接两点的边类型
    queryConnectedEdgesById(Direction direction)     | 查询特定点关联的所有边集合，可指定边的方向和类型
    queryPathFromVertex(Direction direction)         | 查询特定点关联的所有路径集合，可指定边的方向
    queryFixedLengthPathFromVertex(Integer maxHop, Direction direction, Class<?>... edgeClass) | 查询特定点出发的定长路径集合，可指定最大步数、边的方向、边的类型
    queryVariableLengthPathFromVertex(Integer minHop, Integer maxHop,   Direction direction, Class<?>... edgeClass) | 查询特定点出发的变长路径集合，可指定最小步数、最大步数、边的方向、边的类型
    queryShortestPathFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询特定点出发的任意一条最短路径，可指定步数、边的方向、终点实体
    queryAllShortestPathsFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询从该点出发的所有最短路径集合，可指定步数、边的方向、终点实体
    queryVertexCountByTag()                          | 查询特定Tag的点的数量

    - GraphBaseEdge:

    API | 用法说明
    --|--
    queryEdgeByType(Direction direction)             | 查询特定类型、方向的边集合
    queryEdgeWithSrcAndDstByProperties(T srcVertex, Direction direction, T dstVertex) | 查询特定属性的边集合
    queryEdgePropertiesBySrcAndDstId()               | 查询特定始终点id的边集合
    queryEdgeCountByType()                           | 查询特定Type的边的数量

- feat: 修复 [#324](https://github.com/nebulagraph/ngbatis/issues/324) 在 NebulaDaoBasic 中增加 insertForce(v) insertSelectiveForce(v)。[#335](https://github.com/nebula-contrib/ngbatis/pull/335)
- feat: @Space 注解和 Mapper xml 中的 space 配置支持动态配置。 ([#318](https://github.com/nebula-contrib/ngbatis/pull/318), 来自：[@charle004](https://github.com/charle004))
- feat: 支持 yml 中配置多个 mapper-locations。 ([#318](https://github.com/nebulagraph/ngbatis/pull/318), 来自：[@charle004](https://github.com/charle004))
- feat: SessionPool 支持使用 `spaceFromParam`进行运行时的图空间切换，包括运行后才创建的图空间。
