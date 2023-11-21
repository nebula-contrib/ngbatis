package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022- All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import jakarta.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.Space;

/**
 * @author yeweicheng
 * @since 2023-01-12 13:20
 *   <br> Now is history!
 */
@Table(name = "employee")
@Space(name = "test")
public class Employee extends Person {
  public Employee() {
  }

  private String position;

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}
