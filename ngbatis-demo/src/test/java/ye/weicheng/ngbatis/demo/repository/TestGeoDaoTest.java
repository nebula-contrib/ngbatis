package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.JSON;
import java.util.Date;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import ye.weicheng.ngbatis.demo.pojo.TestGeo;

/**
 * @author yeweicheng
 * @since 2025-05-17 11:42
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class TestGeoDaoTest {

  static final int sec = new Date().getSeconds();
  
  static final String caseId = "test-geo";

  @Autowired private TestGeoDao dao;
  
  @Test
  @Order(1)
  public void testInsert() {
    TestGeo geo = new TestGeo();
    geo.setId(caseId);
    geo.setGeoPoint(new Point(sec, sec + 0.1));
    geo.setGeoLine(new org.springframework.data.geo.Box(
      new Point(sec, sec + 0.1),
      new Point(sec + 0.1, sec)
    ));
    geo.setGeoPolygon(new org.springframework.data.geo.Polygon(
      new Point(sec, sec + 0.1),
      new Point(sec + 0.1, sec),
      new Point(sec, sec)
    ));
    dao.insert(geo);
  }
  
  @Test
  @Order(2)
  public void testSelectById() {
    TestGeo testGeo = dao.selectById(caseId);
    System.out.println(JSON.toJSONString(testGeo));
  }
}
