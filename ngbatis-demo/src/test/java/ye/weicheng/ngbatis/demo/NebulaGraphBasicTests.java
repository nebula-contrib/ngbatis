package ye.weicheng.ngbatis.demo;

import java.util.List;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.nebula.contrib.ngbatis.enums.Direction;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import ye.weicheng.ngbatis.demo.pojo.Edge.Follow;
import ye.weicheng.ngbatis.demo.pojo.Vertex.Player;
import ye.weicheng.ngbatis.demo.pojo.Edge.Serve;
import ye.weicheng.ngbatis.demo.pojo.Vertex.Team;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:28
 */
@SpringBootTest
public class NebulaGraphBasicTests {
  @Test
  public void queryVertexId() {
	Player player = new Player();
	//查询tag为Player的点的id
	List<String> ids = player.queryId();
	System.out.println(ids.toString());

	//设置属性
	player.setName("Vince Carter");
	List<String> ids2 = player.queryId();
	System.out.println(ids2.toString());
  }

  @Test
  public void queryVertexByTag() {
	Player player = new Player();
	List<Player> players = player.queryByTag();
	System.out.println(players.toString());
  }

  @Test
  public void queryVertexById() {
	Player player = new Player();
	player.setId("player101");
	Player p = player.queryById();
	System.out.println(p);

	//id为空，报错 Player does not have id.
//		Player player2 = new Player();
//		Player nPlayer2 = player2.queryById();
//		System.out.println(nPlayer2);
  }

  @Test
  public void queryVertexByProperty() {
	Player player = new Player();
	player.setAge(33);
	List<Player> players = player.queryByProperty();
	System.out.println(players.toString());
  }

  @Test
  public void queryAllAdjacentVertex() {
	Player player = new Player();
	player.setAge(36);
	player.setId("player101");
	List<NgVertex<String>> vs = player.queryAllAdjacentVertex(Serve.class);
	System.out.println(JSON.toJSONString(vs));

	List<NgVertex<String>> vs2 = player.queryAllAdjacentVertex();
	for (NgVertex<String> v : vs2) {
	  System.out.println(JSON.toJSONString(v));
	}
  }

  @Test
  public void queryIncomingAdjacentVertex() {
	Player player = new Player();
	player.setAge(37);
	List<NgVertex<String>> res = player.queryIncomingAdjacentVertex();
	System.out.println(res);
  }

  @Test
  public void queryOutgoingAdjacentVertex() {
	Player player = new Player();
	player.setAge(37);
	List<NgVertex<String>> res = player.queryOutgoingAdjacentVertex();
	System.out.println(res);
  }

  @Test
  public void queryNeighborsWithHop() {
	Player player = new Player();
	player.setId("player102");
	List<String> ids = player.queryNeighborsWithHop(2, 2, Follow.class);
	System.out.println(ids);


	Player player2 = new Player();
	player2.setId("player100");
	List<String> ids2 = player2.queryNeighborsWithHop(1, 2, Follow.class);
	System.out.println(ids2);
  }

  @Test
  public void queryConnectedEdges() {
	Player player = new Player();
	player.setId("player110");
	List<NgEdge<String>> ngEdges = player.queryConnectedEdges(Direction.NULL);
	System.out.println(JSON.toJSONString(ngEdges));
	List<NgEdge<String>> ngEdges2 = player.queryConnectedEdges(Direction.BIDIRECT);
	System.out.println(JSON.toJSONString(ngEdges2));
  }


  @Test
  public void queryPathFromVertex() {
	Player player = new Player();
	player.setName("Vince Carter");

	List<NgPath<Object>> ngPaths = player.queryPathFromVertex(Direction.NULL);
	System.out.println(JSON.toJSONString(ngPaths));
  }


  @Test
  public void queryShortestPath() {
	Player player = new Player();
	player.setName("Tim Duncan");

	Player v2 = new Player();
	v2.setName("Tony Parker");

	NgPath<String> ngPath = player.queryShortestPath(5, Direction.NULL, v2);
	System.out.println(JSON.toJSONString(ngPath));
  }


  @Test
  public void queryAllShortestPaths() {
	Player player = new Player();
	player.setName("Tim Duncan");

	Player v2 = new Player();
	v2.setName("Tony Parker");

	List<NgPath<Object>> ngPaths = player.queryAllShortestPaths(5, Direction.NULL, v2);
	System.out.println(JSON.toJSONString(ngPaths));
  }

  @Test
  public void queryFixedLengthPath() {
	Player player = new Player();
	player.setName("Tim Duncan");
	List<NgPath<String>> ngPaths = player.queryFixedLengthPath(5, Direction.NULL, Follow.class, Serve.class);
	System.out.println(JSON.toJSONString(ngPaths));
  }

  @Test
  public void queryVariableLengthPath() {
	Player player = new Player();
	player.setName("Tim Duncan");
	List<NgPath<String>> ngPaths = player.queryVariableLengthPath(1, 3, Direction.NULL, Follow.class);
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
	List<Serve> serves = serve.queryByType(Direction.NULL);
	for (Serve s : serves) {
	  System.out.println(s);
	}
  }

  @Test
  public void queryEdgeByProperty() {
	Serve serve = new Serve();
	serve.setStart_year(2003);
	Player src = new Player();
	src.setName("Carmelo Anthony");
	Team dst = new Team();
	List<Follow> fl = serve.queryByProperty(src, Direction.BIDIRECT, dst);
	System.out.println(JSON.toJSONString(fl));
  }

  @Test
  public void queryEdgeProperty() {
	Serve serve = new Serve();
	serve.setSrcId("player100");
	serve.setDstId("team204");

	Serve s = serve.queryEdgeProperty();
	System.out.println(s);
  }

  @Test
  public void queryEdgeCountByType() {
	Follow follow = new Follow();
	Integer count = follow.queryEdgeCountByType();
	System.out.println(count);
  }

}
