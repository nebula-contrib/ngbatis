package ye.weicheng.ngbatis.demo;


import com.alibaba.fastjson.JSON;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nebula.contrib.ngbatis.enums.Direction;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.edge.Follow;
import ye.weicheng.ngbatis.demo.pojo.edge.Serve;
import ye.weicheng.ngbatis.demo.pojo.vertex.Player;
import ye.weicheng.ngbatis.demo.pojo.vertex.Team;


/**
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:28
 */
@SpringBootTest
public class NebulaGraphBasicTests {
  @Test
  public void queryVertexId() {
    Player player = new Player();
    //直接查Id(根据Tag)
    List<String> ids = player.queryIdsByProperties();
    System.out.println(ids.toString());
    //设置属性
    player.setName("Vince Carter");
    player.setAge(42);
    List<String> ids2 = player.queryIdsByProperties();
    System.out.println(ids2.toString());
  }

  @Test
  public void queryVertexById() {
    Player player = new Player();
    player.setId("player101");
    Player p = player.queryVertexById();
    System.out.println(p);
  }

  @Test
  public void queryVertexByTag() {
    Player player = new Player();
    List<Player> players = player.queryVertexByTag();
    System.out.println(players.toString());
  }

  @Test
  public void queryVertexByProperties() {
    Player player = new Player();
    player.setId("player101");
    player.setAge(36);
    List<Player> players = player.queryVertexByProperties();
    System.out.println(players.toString());
  }

  @Test
  public void queryAllAdjacentVertex() {
    Player player = new Player();
    player.setAge(36);
    player.setId("player101");
    List<NgVertex<String>> vs = player.queryAllAdjacentVertex();
    for (NgVertex<String> v : vs) {
      System.out.println(JSON.toJSONString(v));
    }
    //指定多个边类型
    List<NgVertex<String>> vs2 = player.queryAllAdjacentVertex(Serve.class,Follow.class);
    for (NgVertex<String> v : vs2) {
      System.out.println(JSON.toJSONString(v));
    }
  }

  @Test
  public void queryIncomingAdjacentVertex() {
    Player player = new Player();
    player.setAge(37);
    List<NgVertex<String>> res = player.queryIncomingAdjacentVertex();
    for (NgVertex<String> v : res) {
      System.out.println(JSON.toJSONString(v));
    }
  }

  @Test
  public void queryOutgoingAdjacentVertex() {
    Player player = new Player();
    player.setAge(37);
    List<NgVertex<String>> res = player.queryOutgoingAdjacentVertex();
    for (NgVertex<String> v : res) {
      System.out.println(JSON.toJSONString(v));
    }
  }

  //  @Test
  public void queryNeighborIdsWithHopById() {
    Player player = new Player();
    player.setId("player102");
    List<String> ids = player.queryNeighborIdsWithHopById(2, 2, Follow.class,Serve.class);
    System.out.println(ids);

    Player player2 = new Player();
    player2.setId("player100");
    List<String> ids2 = player2.queryNeighborIdsWithHopById(1, 2);
    System.out.println(ids2);
  }

  @Test
  public void queryConnectedEdgesById() {
    Player player = new Player();
    player.setId("player100");
    List<NgEdge<String>> ngEdges = player.queryConnectedEdgesById(Direction.NULL);
    for (NgEdge<String> ngEdge : ngEdges) {
      System.out.println(JSON.toJSONString(ngEdge));
    }
    List<NgEdge<String>> ngEdges2 = player.queryConnectedEdgesById(Direction.BIDIRECT,Follow.class);
    for (NgEdge<String> ngEdge : ngEdges2) {
      System.out.println(JSON.toJSONString(ngEdge));
    }
  }


  @Test
  public void queryPathFromVertex() {
    Player player = new Player();
    player.setName("Tony Parker");
    player.setId("player101");
    List<NgPath<String>> ngPaths = player.queryPathFromVertex(Direction.NULL);
    for (NgPath<String> ngPath : ngPaths) {
      System.out.println(JSON.toJSONString(ngPath));
    }
  }

  @Test
  public void queryFixedLengthPathFromVertex() {
    Player player = new Player();
    player.setName("Tim Duncan");
    List<NgPath<String>> ngPaths = player.queryFixedLengthPathFromVertex(5, Direction.NULL,
            Follow.class, Serve.class);
    for (NgPath<String> ngPath : ngPaths) {
      System.out.println(JSON.toJSONString(ngPath));
    }
  }


  @Test
  public void queryVariableLengthPath() {
    Player player = new Player();
    player.setName("Tim Duncan");
    List<NgPath<String>> ngPaths = player.queryVariableLengthPathFromVertex(1, 3,Direction.NULL,
            Follow.class);
    for (NgPath<String> ngPath : ngPaths) {
      System.out.println(JSON.toJSONString(ngPath));
    }
  }


  //  @Test
  public void queryShortestPathFromSrcAndDst() {
    Player src = new Player();
    src.setName("Tim Duncan");
    Player dst = new Player();
    dst.setName("Tony Parker");
    NgPath<String> ngPath = src.queryShortestPathFromSrcAndDst(5, Direction.NULL, dst);
    System.out.println(JSON.toJSONString(ngPath));
  }


  //  @Test
  public void queryAllShortestPathsFromSrcAndDst() {
    Player src = new Player();
    src.setName("Tim Duncan");
    Player dst = new Player();
    dst.setName("Tony Parker");
    List<NgPath<Object>> ngPaths = src.queryAllShortestPathsFromSrcAndDst(5, Direction.NULL, dst);
    System.out.println(JSON.toJSONString(ngPaths));
  }



  @Test
  public void queryVertexCountByTag() {
    Player player = new Player();
    Integer count = player.queryVertexCountByTag();
    System.out.println(count);
  }


  @Test
  public void queryEdgeByType() {
    Serve serve = new Serve();
    List<Serve> serves = serve.queryEdgeByType(Direction.NULL);
    for (Serve s : serves) {
      System.out.println(s);
    }
  }

  @Test
  public void queryEdgeWithSrcAndDstByProperties() {
    //边
    Serve serve = new Serve();
    serve.setStartYear(2003);
    //起点
    Player src = new Player();
    src.setId("player129");
    //终点
    Team dst = new Team();
    dst.setName("Heat");
    List<Follow> fl = serve.queryEdgeWithSrcAndDstByProperties(src, Direction.BIDIRECT, dst);
    System.out.println(JSON.toJSONString(fl));
  }

  @Test
  public void queryEdgePropertiesBySrcAndDstId() {
    Serve serve = new Serve();
    serve.setSrcId("player100");
    serve.setDstId("team204");
    Serve s = serve.queryEdgePropertiesBySrcAndDstId();
    System.out.println(s);
  }

  @Test
  public void queryEdgeCountByType() {
    Follow follow = new Follow();
    Integer count = follow.queryEdgeCountByType();
    System.out.println(count);
  }

}
