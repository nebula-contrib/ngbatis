package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 列别名测试用例-实体类
 * @author yeweicheng
 * @since 2022-09-10 9:26
 * <br>Now is history!
 */
@Table(name = "column_alias")
public class ColumnAlias {

  @Id
  @Column(name = "id_no")
  private String idNo;
  
  @Column(name = "first_name")
  private String firstName;
  
  @Column(name = "last_name")
  private String lastName;
  
  @Transient
  private String ignoreMe;

  public String getIdNo() {
    return idNo;
  }

  public void setIdNo(String idNo) {
    this.idNo = idNo;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getIgnoreMe() {
    return ignoreMe;
  }

  public void setIgnoreMe(String ignoreMe) {
    this.ignoreMe = ignoreMe;
  }
}
