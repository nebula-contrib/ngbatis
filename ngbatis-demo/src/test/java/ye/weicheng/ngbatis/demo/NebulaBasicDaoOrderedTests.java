package ye.weicheng.ngbatis.demo;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import java.util.Date;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**
 * @author yeweicheng
 * @since 2024-12-19 06:24
 * <br>Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class NebulaBasicDaoOrderedTests {

  @Autowired
  private TestRepository dao;
  
  private static Random random = new Random();
  
  private static String nameCache = null;
  
  private static Integer ageCache = null;
  
  private static Date birthdayCache = null;
  
  // 数据库定义的默认年龄
  private static Integer defaultAge = 18;
  
  @BeforeAll
  public static void setVars() {
    nameCache = String.valueOf(random.nextInt());
    ageCache = random.nextInt(100);
    birthdayCache = new Date();
  }
  
  
  @Test
  @Order(1)
  public void testInsert() {
    Person person = new Person();
    nameCache = String.valueOf(random.nextInt());
    person.setName(nameCache);
    person.setAge(ageCache);
    person.setBirthday(birthdayCache);
    Integer effect = dao.insert(person);
    Assert.isTrue(effect == 1);
    checkData();
  }
  
  public void checkData() {
    Person person = dao.selectById(nameCache);
    Assert.isTrue(person != null);
    Assert.isTrue(nullSafeEquals(person.getName(), nameCache));
    Assert.isTrue(nullSafeEquals(person.getAge(), ageCache));
    Assert.isTrue(nullSafeEquals(person.getBirthday(), birthdayCache));
  }
  
  @Test
  @Order(3)
  public void testInsertForce() {
    Person person = new Person();
    person.setName(nameCache);
    ageCache = ageCache + 1;
    person.setAge(ageCache);
    birthdayCache = null;
    person.setBirthday(null);
    Integer effect = dao.insertForce(person);
    Assert.isTrue(effect == 1);
    checkData();
  }
  
  @Test
  @Order(4)
  public void testInsertSelective() {
    testDeleteById();
    setVars();
    Person person = new Person();
    person.setName(nameCache);
    person.setAge(ageCache);
    birthdayCache = null;
    Integer effect = dao.insertSelective(person);
    Assert.isTrue(effect == 1);
    checkData();
  }
  
  @Test
  @Order(5)
  public void testInsertSelectiveForce() {
    Person person = new Person();
    person.setName(nameCache);
    ageCache = defaultAge;
    birthdayCache = new Date();
    person.setAge(ageCache);
    person.setBirthday(birthdayCache);
    Integer effect = dao.insertSelectiveForce(person);
    Assert.isTrue(effect == 1);
    checkData();
  }
  
  @Test
  @Order(1000)
  public void testDeleteById() {
    Integer effect = dao.deleteById(nameCache);
    Assert.isTrue(effect == 1);
    Person person = dao.selectById(nameCache);
    Assert.isTrue(person == null);
  }

}
