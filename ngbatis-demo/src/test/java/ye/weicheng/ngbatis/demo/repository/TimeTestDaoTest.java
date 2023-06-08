package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import ye.weicheng.ngbatis.demo.pojo.TimeTest;

/**
 * @author yeweicheng
 * @since 2023-06-07 17:19
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class TimeTestDaoTest {
  
  static final String caseId = "time-test"; 
  
  static final Date datetime = new Date();
  
  static final java.sql.Date date = new java.sql.Date(datetime.getTime());
  
  static final java.sql.Timestamp timestamp = new java.sql.Timestamp(datetime.getTime());
  
  static final java.sql.Time time = new java.sql.Time(datetime.getTime());
  
  @Autowired private TimeTestDao dao;

  @Test
  @Order(1)
  public void testInsert() {
    TimeTest test = new TimeTest();
    test.setId(caseId);
    test.setDatetime(datetime);
    test.setDate(date);
    test.setTime(time);
    test.setTimestamp(timestamp);
    test.setDuration(java.time.Duration.ofMillis(2000));
    dao.insert(test);
  }
  
  @Test
  @Order(2)
  public void selectById() {
    TimeTest timeTest = dao.selectById(caseId);
    Assert.isTrue(
      Objects.equals(timeTest.getDate().toString(), date.toString()),
      "Date must be equal to the value before insertion"
    );
    
    Assert.isTrue(
      Objects.equals(timeTest.getTime().toString(), time.toString()),
      "Time must be equal to the value before insertion"
    );
    
    Assert.isTrue(
      Objects.equals(timeTest.getDatetime().toString(), datetime.toString()),
      "Datetime must be equal to the value before insertion"
    );
    
    String dbTimestamp = String.valueOf(timeTest.getTimestamp().getTime());
    // 毫秒精度丢失
    String timestampInsertBefore = (timestamp.getTime() / 1000) + "000";
    Assert.isTrue(
      Objects.equals(dbTimestamp, timestampInsertBefore),
      "Timestamp must be equal to the value before insertion"
    );
    
    Assert.isTrue(
      timeTest.getDuration().getSeconds() == 2, 
      "2000ms must be equal 2s"
    );
  }
  
  @Test
  @Order(3)
  public void deleteById() {
    dao.deleteById(caseId);
  }
  
  @Test
  @Order(4)
  public void selectTenDaysTwoSec() {
    Duration o = dao.selectTenDaysTwoSec();
    Assert.isTrue(o.getSeconds() == 864002, "10d 2s equals 864002s");
  }
  
}
