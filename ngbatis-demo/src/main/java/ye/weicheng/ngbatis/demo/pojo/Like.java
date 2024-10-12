package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.DstId;
import org.nebula.contrib.ngbatis.annotations.SrcId;

/**
 * <p>关系实体类示例。</p>
 * @author yeweicheng
 * @since 2022-06-21 17:08
 * <br>Now is history!
 */
@Table(name = "like")
public class Like {

  @Id // 可选，如果两个节点之间同一类型边的唯一性由源节点id和目标节点id共同决定，可以不加当前属性
  private Long rank;
  
  @SrcId // 可选，如果不需要获取关系的源节点id，可以不加当前属性
  private String srcId;
  
  @DstId // 可选，如果不需要获取关系的目标节点id，可以不加当前属性
  private String dstId;
  
  private Double likeness;

  public Double getLikeness() {
    return likeness;
  }

  public void setLikeness(Double likeness) {
    this.likeness = likeness;
  }
}
