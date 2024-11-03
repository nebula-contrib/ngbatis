# 实体直查
这里具体使用的是NebulaGraph官方提供的[示例数据Basketballplayer](https://docs.nebula-graph.com.cn/3.8.0/3.ngql-guide/1.nGQL-overview/1.overview/#basketballplayer)

## 自定义点或边实体

### 点实体
- 继承`GraphBaseVertex`类标识是点实体
- `@Tag`的name属性注明点实体的Tag
- `@GraphId`的type属性注明点实体id的类型（可选）
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
### 边实体
- 继承`GraphBaseEdge`类标识是边实体
- `@EdgeType`的name属性注明边实体的类型
- `@Id`（可选，如果两个节点之间同一类型边的唯一性由源节点id和目标节点id共同决定，可以不加当前属性）
- `@SrcId`（可选，如果不需要获取关系的源节点id，可以不加当前属性）
- `@DstId`（可选，如果不需要获取关系的目标节点id，可以不加当前属性）
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

## 使用示例

```java
@Test
public void testVertex(){
  Player srcPlayer = new Player();
  //查询所有符合条件 name = "Vince Carter" 的Player顶点
  srcPlayer.setName("Vince Carter");
  List<Player> vertices = player.queryVertexByProperties();
}

@Test
public void testEdge(){
  Serve serve = new Serve();
  //查询起点id为player100，终点id为team204的Serve边
  serve.setSrcId("player100");
  serve.setDstId("team204");
  Serve edge = serve.queryEdgeWithSrcAndDstByProperties();
  //查询Serve类型、方向为”->“的边
  List<Serve> edges = serve.queryEdgeByType(Direction.NULL);
}
```
