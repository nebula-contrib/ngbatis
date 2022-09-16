package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.junit.jupiter.api.Test;
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
public class ColumnAliasDaoTest {
    
  @Autowired
  private ColumnAliasDao dao;

  @Test
  public void updateById() {
    long now = System.currentTimeMillis();
    String idNo = "UBI" + now;
    ColumnAlias colAliasPojo = new ColumnAlias();
    colAliasPojo.setIdNo(idNo);
    dao.insert(colAliasPojo);
    
    String firstName = Math.random() + "";
    colAliasPojo.setFirstName(firstName);
    dao.updateById(colAliasPojo);
    
    ColumnAlias colAliasPojoDb = dao.selectById(idNo);

    Assert.isTrue(firstName.equals(colAliasPojoDb.getFirstName()));
  }

}
