# Features

## A. Integrate

- [x] Support the rapid integration of nebula graph and springboot through simple configuration

## B. Single Table (vertex, edge) Operation, No Need to Write `nGQL | cypher`

> See【[By Basic DAO](../dev-example/dao-basic)】

| API                                              | Instructions                                                                         |
| ------------------------------------------------ | ------------------------------------------------------------------------------------ |
| insert(T entity)                                 | Insert vertex, null value override                                                   |
| insertSelective(T entity)                        | Insert vertex, write only non empty properties.                                      |
| selectById(ID id)                                | Get vertex by primary key                                                            |
| selectBySelective(T entity)                      | Query by non empty properties                                                        |
| insertEdge(S startNode, R edge, E endNode)       | Establish the relationship between two nodes                                         |
| `selectPage(Page<T> page)`                         | Paging query                                                                         |
| existsEdge(ID startId, Class edgeType, ID endId) | Make sure whether there is a certain relationship between the two vertexes           |
| listStartNodes(Class edgeType, ID endId)         | Find all upstream vertexes in a certain relationship through a specific vertex       |
| startNode(Class edgeType, ID endId)              | Find the unique upstream vertex of a specific relationship through a specific vertex |

## C. Use XML to Centrally Manage `nGQL | cypher`

> See【[By Custom nGQL](../dev-example/custom-crud)】

Scan the specified resource package, obtain the `nGQL | cypher` template, and operate on the basis of the template.

### a. Parameter substitution

- [x] replace parameters with placeholder `nGQL | cypher` and execute to the database;
  - Write query script template and match parameter control to realize dynamic query
  - Realize batch operation through parameter loop

### b. Process the `ResultSet` through the method signature information of the Dao interface to form the type required by the business

- [x] Types of Collection

  - `Collection<Types of Basic>`
  - `Collection<Types of Object>` The object type refers to the following object support:

- [x] Types of Basic

  - String
  - Boolean
  - Number (Integer, Long, Float, Double, Byte, Short). **Only wrapper classes are supported for the time being**

- [x] Types of Object

  - Object

  - Multiple column return values are converted to map
  - Multiple column return values are converted to POJO
  - Support vertex type conversion to POJO
  - Support edge type conversion to POJO

- [x] ResultSet If you do not need to use the result processing provided by the framework, you can directly declare the return value `ResultSet` on the interface and process it yourself
## D. Use the provided method to conduct entity direct inspection
> See【[Entity Direct Query](../dev-example/entity-query)】

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

## E. Interface of Primary key generation

- [x] Provide the embedding point of the primary key generator, and developers can customize the primary key generator.
