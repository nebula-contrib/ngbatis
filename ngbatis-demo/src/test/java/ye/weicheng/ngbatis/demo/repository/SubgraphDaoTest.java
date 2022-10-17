package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Subgraph;

/**
 * @author yeweicheng
 * @since 2022-09-22 16:05
 * <br>Now is history!
 */
@SpringBootTest
class SubgraphDaoTest {

  @Autowired private SubgraphDao dao;
    
  @Test
  public void subgraph() {
    List<Subgraph> subgraph = dao.subgraph();
    System.out.println(subgraph);
  }
    
}
