# 框架特性

## 一、集成

- [x] 支持通过简单配置，快速完成 Nebula Graph 与 Springboot 的整合

## 二、单表（Vertex、Edge）操作，无需写  `nGQL | cypher`
>
> 用法参见【[使用基类编写](../dev-example/dao-basic)】  

API | 用法说明
--|--
selectById(ID id) | 通过主键获取节点
selectByIds(Collection<I\> ids) | 根据 id 集合获取节点
selectBySelective(T entity) | 按实体属性值查询
selectIdBySelectiveStringLike(T entity) | 根据实体属性值查询，字符串属性使用模糊查询
selectByMap(Map<String, Object\> param) | 根据 map 参数查询
countByMap(Map<String, Object\> param) | 根据 map 参数统计条数
selectPage(Page<T\> page) | 分页查询
insert(T entity) | 插入 Vertex，空值覆盖
insertSelective(T entity) | 插入 Vertex，空值跳过
insertBatch(List<T\> list) | 批量插入 Vertex
updateById(T entity) | 根据 id 值进行更新，空值覆盖
updateByIdSelective(T entity) | 根据 id 值进行更新，空值跳过，保留数据库原值
updateByIdBatchSelective(List<T\> entities) | 批量更新，属性空值跳过，保留数据库原值
deleteWithEdgeById(I id) | 根据 id 值，删除节点与关系
deleteById(I id) | 根据 id 值，删除节点（保留悬挂边）
insertEdge(S startNode, R edge, E endNode) | 插入关系
existsEdge(ID startId, Class edgeType, ID endId) | 判断两个节点是否有关系
listStartNodes(Class edgeType, ID endId) | 查找一个节点某种关系中的所有上游节点
listStartNodes(Class<E\> startType, Class edgeType, ID endId) | 查找一个节点某种关系中的特定类型的上游节点
startNode(Class edgeType, ID endId) | 查找一个节点中，某种关系的唯一一个上游节点
startNode(Class<E\> startType, Class edgeType, ID endId) | 查找查找一个节点特定类型的上游节点

## 三、使用 xml 的方式，集中管理  `nGQL | cypher`
>
> 用法参见【[自定义nGQL](../dev-example/custom-crud)】  

扫描指定资源包，并获得 `nGQL | cypher` 模板，在模板的基础上做操作。

### (一) 参数替换

- [x] 使用占位符为 `nGQL | cypher` 替换参数，并执行到数据库;
  - 编写查询脚本模板，搭配参数控制，实现动态查询
  - 通过参数循环，实现批量操作

### (二) 通过 Dao 接口的方法签名信息，对 ResultSet 进行处理，形成业务所需类型

- [x] 集合类型
  - Collection<基本类型>
  - Collection<对象类型> `Object类型参考下述Object的支持`
- [x] 基本类型
  - String
  - Boolean
  - Number （Integer、Long、Float、Double、Byte、Short）。**暂时只支持包装类**
- [x] 对象类型
  - Object
  - 多列return值转换成 Map
  - 多列return值转换成 POJO
  - 支持Vertex类型转换成 POJO
  - 支持Edge类型转换成 POJO
- [x] ResultSet 如不需要使用框架自带的结果处理，可直接在接口声明返回值 ResultSet 并自行处理
## 四、使用提供的方法进行实体直查
>
> 用法参见【[实体直查](../dev-example/entity-query.md)】


| API                                                          | 用法说明                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| queryIdsByProperties()                                       | 查询特定Tag或者属性的点Id集合                                |
| queryVertexById()                                            | 查询特定点Id的单个点                                         |
| queryVertexByTag()                                           | 查询特定Tag的点集合                                          |
| queryVertexByProperties()                                    | 查询特定属性的点集合                                         |
| queryAllAdjacentVertex(Class<?>... edgeClass)                | 查询特定点的所有邻点集合，可指定一个或多个连接两点的边类型   |
| queryIncomingAdjacentVertex(Class<?>... edgeClass)           | 查询特定点入边方向的邻点集合，可指定一个或多个连接两点的边类型 |
| queryOutgoingAdjacentVertex(Class<?>... edgeClass)           | 查询特定点出边方向的邻点集合，可指定一个或多个连接两点的边类型 |
| queryNeighborIdsWithHopById(int m, int n, Class<?>... edgeClass) | 查询特定点指定跳数内的点Id集合，可指定一个或多个连接两点的边类型 |
| queryConnectedEdgesById(Direction direction)                 | 查询特定点关联的所有边集合，可指定边的方向和类型             |
| queryPathFromVertex(Direction direction)                     | 查询特定点关联的所有路径集合，可指定边的方向                 |
| queryFixedLengthPathFromVertex(Integer maxHop, Direction direction, Class<?>... edgeClass) | 查询特定点出发的定长路径集合，可指定最大步数、边的方向、边的类型 |
| queryVariableLengthPathFromVertex(Integer minHop, Integer maxHop,   Direction direction, Class<?>... edgeClass) | 查询特定点出发的变长路径集合，可指定最小步数、最大步数、边的方向、边的类型 |
| queryShortestPathFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询特定点出发的任意一条最短路径，可指定步数、边的方向、终点实体 |
| queryAllShortestPathsFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询从该点出发的所有最短路径集合，可指定步数、边的方向、终点实体 |
| queryVertexCountByTag()                                      | 查询特定Tag的点的数量                                        |
| queryEdgeByType(Direction direction)                         | 查询特定类型、方向的边集合 |
| queryEdgeWithSrcAndDstByProperties(T srcVertex, Direction direction, T dstVertex) | 查询特定属性的边集合       |
| queryEdgePropertiesBySrcAndDstId()                           | 查询特定始终点id的边集合   |
| queryEdgeCountByType()                                       | 查询特定Type的边的数量     |

## 五、主键生成策略接口

- [x] 提供主键生成器的埋点，开发者可自定义主键生成器。
