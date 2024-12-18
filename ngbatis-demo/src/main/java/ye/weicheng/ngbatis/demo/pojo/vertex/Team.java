package ye.weicheng.ngbatis.demo.pojo.vertex;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.persistence.Id;
import org.nebula.contrib.ngbatis.annotations.base.Tag;
import org.nebula.contrib.ngbatis.base.GraphBaseVertex;


/**
 * Team点实体示例
 * @author xYLiuuuuuu
 * @since 2024/9/11
 */

@Tag(name = "team")
public class Team extends GraphBaseVertex {

  @Id
  private String id;

  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Team{" + "name='" + name + '\'' + '}';
  }
}
