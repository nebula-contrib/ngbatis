package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * <p>关系实体类示例。</p>
 * @author yeweicheng
 * @since 2022-06-21 17:08
 * <br>Now is history!
 */
@Table(name = "like")
public class LikeWithRank {

  @Id
  private Long rank;

  private Double likeness;

  public Double getLikeness() {
    return likeness;
  }

  public void setLikeness(Double likeness) {
    this.likeness = likeness;
  }

  public Long getRank() {
    return rank;
  }

  public void setRank(Long rank) {
    this.rank = rank;
  }
}
