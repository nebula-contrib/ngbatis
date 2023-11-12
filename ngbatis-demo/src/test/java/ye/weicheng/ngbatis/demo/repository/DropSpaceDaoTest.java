package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2023 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yeweicheng
 * @since 2023-11-10 13:26
 * <br>Now is history!
 */
@SpringBootTest
class DropSpaceDaoTest {

  @Autowired
  private DropSpaceDao dao;
  
  @Test
  void dropSpace() throws InterruptedException {
    String spaceName = "test_drop";
    dao.createSpace(spaceName);
    Thread.sleep(10 * 1000);
    
    List<String> tags = dao.showTags();
    System.out.println(tags);
    
    dao.dropSpace(spaceName);
  }

}
