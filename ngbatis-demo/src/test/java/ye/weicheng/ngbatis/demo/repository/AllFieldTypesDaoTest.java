package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import ye.weicheng.ngbatis.demo.pojo.AllFieldTypes;

/**
 * @author yeweicheng
 * @since 2025-05-07 3:48
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class AllFieldTypesDaoTest {
  
  @Autowired private AllFieldTypesDao dao;

  @Test
  void testValueFmt() {
    AllFieldTypes allFieldTypes = new AllFieldTypes();
    allFieldTypes.setaLong(1L);
    allFieldTypes.setaBoolean(true);
    allFieldTypes.setaString("aString");
    allFieldTypes.setaDouble(1.0);
    allFieldTypes.setAnInt(1);
    allFieldTypes.setaShort((short) 1);
    allFieldTypes.setaByte((byte) 1);
    allFieldTypes.setaFloat(1.0f);
    allFieldTypes.setaDate(new java.sql.Date(System.currentTimeMillis()));
    allFieldTypes.setaTime(new java.sql.Time(System.currentTimeMillis()));
    allFieldTypes.setaDateTime(new java.util.Date(System.currentTimeMillis()));
    allFieldTypes.setaTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
    allFieldTypes.setaDuration(java.time.Duration.ofSeconds(1));

    AllFieldTypes allFieldTypesFromDb = dao.testValueFmt(allFieldTypes);
    System.out.println(JSON.toJSONString(allFieldTypes));
    System.out.println(JSON.toJSONString(allFieldTypesFromDb));
    System.out.println(allFieldTypesFromDb.equals(allFieldTypes)); // 两个对象不相等，因为时间精度失真
  }

  @Test
  void testValueFmtAllNull() {
    AllFieldTypes allFieldTypes = new AllFieldTypes();
    AllFieldTypes allFieldTypesFromDb = dao.testValueFmt(allFieldTypes);
    System.out.println(JSON.toJSONString(allFieldTypesFromDb));
    System.out.println(JSON.toJSONString(allFieldTypesFromDb));
    Assert.isTrue(nullSafeEquals(allFieldTypesFromDb, allFieldTypes), "two var should be equal!"); // 断言两个对象相等
  }
}