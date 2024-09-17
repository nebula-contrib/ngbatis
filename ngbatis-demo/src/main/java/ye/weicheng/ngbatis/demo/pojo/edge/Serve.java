package ye.weicheng.ngbatis.demo.pojo.edge;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.persistence.Id;
import org.nebula.contrib.ngbatis.annotations.DstId;
import org.nebula.contrib.ngbatis.annotations.SrcId;
import org.nebula.contrib.ngbatis.annotations.base.EdgeType;
import org.nebula.contrib.ngbatis.base.GraphBaseEdge;

/**
 * Serve边实体示例
 * @author xYLiuuuuuu
 * @since 2024/9/11
 */
@EdgeType(name = "serve")
public class Serve extends GraphBaseEdge {

  @Id // 可选，如果两个节点之间同一类型边的唯一性由源节点id和目标节点id共同决定，可以不加当前属性
  private Long rank;

  @SrcId // 可选，如果不需要获取关系的源节点id，可以不加当前属性
  private String srcId;

  @DstId // 可选，如果不需要获取关系的目标节点id，可以不加当前属性
  private String dstId;

  private Integer start_year;
  private Integer end_year;

  public Long getRank() {
    return rank;
  }

  public void setRank(Long rank) {
    this.rank = rank;
  }

  public String getSrcId() {
    return srcId;
  }

  public void setSrcId(String srcId) {
    this.srcId = srcId;
  }

  public String getDstId() {
    return dstId;
  }

  public void setDstId(String dstId) {
    this.dstId = dstId;
  }

  public int getStart_year() {
    return start_year;
  }

  public void setStart_year(int start_year) {
    this.start_year = start_year;
  }

  public int getEnd_year() {
    return end_year;
  }

  public void setEnd_year(int end_year) {
	this.end_year = end_year;
  }


  @Override
  public String toString() {
	return "Serve{" +
			"rank=" + rank +
			", srcId='" + srcId + '\'' +
			", dstId='" + dstId + '\'' +
			", start_year=" + start_year +
			", end_year=" + end_year +
			'}';
  }
}
