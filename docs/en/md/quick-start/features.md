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
| queryIdsByProperties()                                       | Query a collection of vertex ids for a particular Tag or attribute |
| queryVertexById()                                            | Query a single vertex for a specific vertex Id               |
| queryVertexByTag()                                           | Query a collection of vertices  for a specific Tag           |
| queryVertexByProperties()                                    | Query a collection of vertexes for a specific property       |
| queryAllAdjacentVertex(Class<?>... edgeClass)                | Query a collection of all neighboring vertexes of a particular vertex, specifying one or more edge types that connect the two vertexes |
| queryIncomingAdjacentVertex(Class<?>... edgeClass)           | Query the set of adjacent vertexes in the direction of the incoming edge of a particular vertex, specifying one or more edge types that connect two vertexes |
| queryOutgoingAdjacentVertex(Class<?>... edgeClass)           | Query the set of adjacent vertexes in the direction of the edge of a particular vertex, specifying one or more edge types that connect two vertexes |
| queryNeighborIdsWithHopById(int m, int n, Class<?>... edgeClass) | Query a collection of vertex ids within a specified number of hops for a particular vertex, specifying one or more edge types that connect two vertexes |
| queryConnectedEdgesById(Direction direction)                 | Query the set of all edges associated with a particular vertex, specifying the direction and type of the edge |
| queryPathFromVertex(Direction direction)                     | Query the collection of all paths associated with a particular vertex, specifying the direction of the edge |
| queryFixedLengthPathFromVertex(Integer maxHop, Direction direction, Class<?>... edgeClass) | Query a set of fixed-length paths from a specific vertex, specifying the maximum number of steps, the direction of the edge, and the type of the edge |
| queryVariableLengthPathFromVertex(Integer minHop, Integer maxHop,   Direction direction, Class<?>... edgeClass) | Query a set of variable-length paths from a specific vertex, specifying the minimum number of steps, the maximum number of steps, the direction of the edge, and the type of the edge |
| queryShortestPathFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | Query any shortest path from a specific vertex, specifying the number of steps, the direction of the edge, and the end vertex entity |
| queryAllShortestPathsFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | Query the set of all shortest paths from this vertex, specifying the number of steps, the direction of the edge, and the end vertex entity |
| queryVertexCountByTag()                                      | Query the number of vertexes for a specific Tag              |
| queryEdgeByType(Direction direction)                         | Query a set of edges of a specific type and direction |
| queryEdgeWithSrcAndDstByProperties(T srcVertex, Direction direction, T dstVertex) | Query a set of edges for a particular property        |
| queryEdgePropertiesBySrcAndDstId()                           | Query a set of edges for a specific always vertex id  |
| queryEdgeCountByType()                                       | Query the number of edges for a specific Type         |

## E. Interface of Primary key generation

- [x] Provide the embedding point of the primary key generator, and developers can customize the primary key generator.
