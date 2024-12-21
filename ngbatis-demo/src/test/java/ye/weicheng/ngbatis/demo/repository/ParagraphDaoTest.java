package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Paragraph;

/**
 * @author yeweicheng
 * @since 2024-12-19 9:37
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ParagraphDaoTest {
  @Autowired
  private ParagraphDao dao;
  
  @Test
  @Order(1)
  public void testSpace() {
    ResultSet rs = dao.testSpaceFromYml();
    Assert.equals(rs.getSpaceName(), "test");
  }
  
  @Test
  @Order(2)
  public void testSpaceFromParam() {
    ResultSet rs = dao.testSpaceFromParam("test");
    Assert.equals(rs.getSpaceName(), "test");
  }
  
  @Test
  @Order(3)
  public void testSpaceFromParam2() {
    ResultSet rs = dao.testSpaceFromParam("${nebula.ngbatis.test-space-placeholder}");
    Assert.equals(rs.getSpaceName(), "cmqa");
  }

  @Test
  @Order(4)
  public void testSpaceAnno() {
    ResultSet rs = dao.testSpaceAnno();
    Assert.equals(rs.getSpaceName(), "cmqa");
  }
  
  @Test
  @Order(5)
  public void testSelectById() {
    Paragraph pageable = dao.selectById(0);
    // 观察日志的 session space, 为 cmqa
  }
  
}
