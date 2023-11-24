package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022- All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Multi tags tests.
 * @author yeweicheng
 * @since 2023-01-12 14:04
 *   <br> Now is history!
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class EmployeeDaoTest {
  
  @Autowired private EmployeeDao dao;
  @Autowired private TestRepository personDao;

  static List<String> ids = Arrays.asList(
      "TestMultiTag",
      "insertSelectiveMT",
      "TestMultiTagBatch0",
      "TestMultiTagBatch1",
      "TestMultiTagBatch2"
  );
  
  @Test
  @Order(1)
  public void insert_multiTag() {
    Assert.isTrue(dao.selectByIds(ids).size() == 0);
    Employee employee = new Employee();
    employee.setName("TestMultiTag");
    employee.setPosition("Leader");
    employee.setAge(25);
    dao.insert(employee);
  }

  @Test
  @Order(2)
  public void insertSelective_multiTags() {
    Employee employee = new Employee();
    employee.setName("insertSelectiveMT");
    employee.setPosition("Leader");
    employee.setAge(25);
    dao.insertSelective(employee);
  }

  @Test
  @Order(3)
  public void insertBatch_multiTags() {
    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Employee employee = new Employee();
      employee.setName("TestMultiTagBatch" + i);
      employee.setPosition("Leader");
      employee.setAge(25 + i);
      employee.setBirthday(new Date());
      employees.add(employee);
    }
    dao.insertBatch(employees);
  }
  
  @Test
  @Order(4)
  public void selectByIds() {
    List<Employee> multiTagVertexes = dao.selectByIds(ids);
    System.out.println(JSON.toJSONString(multiTagVertexes));
  }
  
  @Test
  @Order(5)
  public void updateById_multiTags() {
    Integer rd = new Random().nextInt();
    String position = "Leader" + rd;
    Employee employee = new Employee();
    employee.setName("TestMultiTag");
    employee.setPosition(position);
    employee.setAge(rd);
    employee.setBirthday(new Date());
    dao.updateById(employee);
  }

  @Test
  @Order(6)
  public void upsertByIdSelective_multiTags() {
    Integer rd = new Random().nextInt();
    String position = "Leader" + rd;
    Employee employee = new Employee();
    employee.setName("TestMultiTag");
    employee.setPosition(position);
    employee.setAge(rd);
    employee.setBirthday(new Date());
    dao.upsertByIdSelective(employee);
  }

  @Test
  @Order(7)
  public void updateByIdSelective_multiTags() {
    Integer rd = new Random().nextInt();
    String position = "Leader" + rd;
    Employee employee = new Employee();
    employee.setName("insertSelectiveMT");
    employee.setPosition(position);
    employee.setAge(rd);
    employee.setBirthday(new Date());
    dao.updateByIdSelective(employee);
  }

  @Test
  @Order(8)
  public void updateByIdBatchSelective_multiTags() {
    List<Employee> employees = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Integer rd = new Random().nextInt();
      Employee employee = new Employee();
      employee.setName("TestMultiTagBatch" + i);
      employee.setPosition("Leader" + rd);
      employee.setAge(25 + i);
      employee.setBirthday(new Date());
      employees.add(employee);
    }
    dao.updateByIdBatchSelective(employees);
  }

  @Test
  @Order(9)
  public void selectByIdsForUpdate() {
    List<Employee> multiTagVertexes = dao.selectByIds(ids);
    System.out.println(JSON.toJSONString(multiTagVertexes));
  }

  @Test
  @Order(99)
  public void deleteById() {
    ids.forEach(dao::deleteById);
  }
}
