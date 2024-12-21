package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.persistence.Id;
import javax.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.Space;

@Table(name = "paragraph")
@Space(name = "${nebula.ngbatis.test-space-placeholder}")
public class Paragraph {

  @Id
  private Long id;

  private String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
