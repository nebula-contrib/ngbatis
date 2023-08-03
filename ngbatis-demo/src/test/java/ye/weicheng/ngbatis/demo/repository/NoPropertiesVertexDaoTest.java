package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.NoPropertiesVertex;

/**
 * @author yeweicheng
 * @since 2023-08-02 18:39
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class NoPropertiesVertexDaoTest {
  @Autowired
  private NoPropertiesVertexDao dao;

  @Test
  public void insert() {
    NoPropertiesVertex npv = new NoPropertiesVertex();
    dao.insert(npv);
    NoPropertiesVertex noPropertiesVertex = dao.selectById(npv.getName());
    System.out.println(noPropertiesVertex);
    dao.deleteById(noPropertiesVertex.getName());
  }
  
  // update interface doesn't work. 
  // I think any others will not update the no empty vertex ?

}
