package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.ColumnAlias;

/**
 * @author yeweicheng
 * @since 2022-09-10 9:38
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class ColumnAliasDaoTest {
    
  @Autowired
  private ColumnAliasDao dao;
  static final String idNo = "ID" + System.currentTimeMillis();
  static final String firstName = Math.random() + "";

  @Test
  @Order(1)
  public void insert() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    dao.insert(colAliasPojo);
  }
    
  @Test
  @Order(2)
  public void updateById() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    colAliasPojo.setFirstName(firstName);
    dao.updateById(colAliasPojo);
  }
  
  @Test
  @Order(3)
  public void selectById() {
    ColumnAlias colAliasPojoDb = dao.selectById(idNo);
    Assert.isTrue(firstName.equals(colAliasPojoDb.getFirstName()));
    Assert.isTrue(idNo.equals(colAliasPojoDb.getIdNo()));
  }
  
  @Test
  @Order(4)
  public void selectBySelective() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    colAliasPojo.setFirstName(firstName);
    List<ColumnAlias> nodesInDb = dao.selectBySelective(colAliasPojo);
    Assert.isTrue(nodesInDb.size() > 0);
    nodesInDb.forEach(node -> {
      Assert.isTrue(node.getFirstName().equals(firstName));
      Assert.isTrue(node.getIdNo().equals(idNo));
    });
  }

  @Test
  @Order(5)
  public void selectIdBySelective() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    colAliasPojo.setFirstName(firstName);
    List<String> idsInDb = dao.selectIdBySelective(colAliasPojo);
    Assert.isTrue(idsInDb.contains(idNo));
  }
  
  @Test
  @Order(6)
  public void selectBySelectiveStringLike() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    String query = firstName.substring(0, 5);
    colAliasPojo.setFirstName(query);
    List<ColumnAlias> nodesInDb = dao.selectBySelectiveStringLike(colAliasPojo);
    Assert.isTrue(nodesInDb.size() > 0);
    nodesInDb.forEach(node -> Assert.isTrue(node.getFirstName().contains(query)));
  }
  
  // selectIdBySelectiveStringLike
  @Test
  @Order(7)
  public void selectIdBySelectiveStringLike() {
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    String query = firstName.substring(0, 5);
    colAliasPojo.setFirstName(query);
    List<String> idsInDb = dao.selectIdBySelectiveStringLike(colAliasPojo);
    Assert.isTrue(idsInDb.contains(idNo));
  }
  
  @Test
  @Order(99)
  public void deleteById() {
    dao.deleteById(idNo);
  }
  
  @Test
  @Order(100)
  public void selectByIdAfterDelete() {
    ColumnAlias colAliasPojoDb = dao.selectById(idNo);
    Assert.isTrue(colAliasPojoDb == null);
  }

}
