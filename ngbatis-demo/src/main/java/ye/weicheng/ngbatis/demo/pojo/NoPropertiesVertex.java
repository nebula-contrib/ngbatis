package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.persistence.Id;
import javax.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.Space;

/**
 * @author yeweicheng
 * @since 2023-08-02 18:35
 * <br>Now is history!
 */
@Table(name = "no_properties_vertex")
@Space(name = "test")
public class NoPropertiesVertex {
  @Id
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
