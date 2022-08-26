// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

/**.
 * @author yeweicheng.
 * @since 2022-06-21 17:08 <br>.
 *     Now is history.
.*/
@Table(name = "like")
public class LikeWithRank {
  @Id private Long rank;

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
