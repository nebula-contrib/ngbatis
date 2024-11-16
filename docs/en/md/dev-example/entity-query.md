# Entity Direct Query

Specific use NebulaGraph official [Example data Basketballplayer](https://docs.nebula-graph.io/3.8.0/3.ngql-guide/1.nGQL-overview/1.overview/#example_data_basketballplayer)

## Custom vertex or edge entities

### Vertex Entity

- Extends the `GraphBaseVertex` class identifier as a vertex entity
- The name attribute of `@Tag` indicates the Tag of the vertex entity
- The type attribute of `@GraphId` indicates the type of the point entity id (optional)

```java

@Tag(name = "player")
public class Player extends GraphBaseVertex {

  @GraphId(type = IdType.STRING)
  private String id;

  private String name;

  private Integer age;
    
  ...

}

```

### Edge Entity

- Extends the `GraphBaseEdge` class to identify edge entities
- The name attribute of `@EdgeType` indicates the type of the edge entity
- `@Id` (Optional, if the uniqueness of an edge of the same type between two nodes is determined by the source node id and the destination node id, the current attribute can be omitted)
- `@SrcId` (optional, if you do not need to obtain the source node id of the relationship, you can omit the current attribute)
- `@DstId` (Optional, if you do not need to get the target node id of the relationship, you can omit the current attribute)

```java

@EdgeType(name = "serve")
public class Serve extends GraphBaseEdge {

  @Id 
  private Long rank;

  @SrcId 
  private String srcId;

  @DstId 
  private String dstId;

  @Column(name = "start_year")
  private Integer startYear;
  
  @Column(name = "end_year")
  private Integer endYear;

  ...
}

```

## Usage Example

```java

@Test
public void testVertex(){
  
  Player srcPlayer = new Player();
  //Query all Player vertices that meet the condition name = "Vince Carter"
  srcPlayer.setName("Vince Carter");
  List<Player> vertices = player.queryVertexByProperties();

}

@Test
public void testEdge(){
  
  Serve serve = new Serve();
  //Query the Server edge whose starting point ID is player100 and the end point ID is team204.
  serve.setSrcId("player100");
  serve.setDstId("team204");
  Serve edge = serve.queryEdgeWithSrcAndDstByProperties();
  //Query the edges of Serve type and direction "->"
  List<Serve> edges = serve.queryEdgeByType(Direction.NULL);

}

```
