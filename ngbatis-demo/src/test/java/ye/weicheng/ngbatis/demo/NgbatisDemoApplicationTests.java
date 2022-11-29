package ye.weicheng.ngbatis.demo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.nebula.contrib.ngbatis.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.pojo.PersonLikePerson;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

@SpringBootTest
class NgbatisDemoApplicationTests {

  @Autowired
  private TestRepository repository;

  @Test
  void selectPerson() {
    Object person = repository.selectPerson();
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  void selectPersonMap() {
    Map person = repository.selectPersonMap();
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  void selectPersons() {
    List<Person> persons = repository.selectPersons();
    System.out.println(JSON.toJSONString(persons));
  }

  @Test
  void selectPersonsMap() {
    List<Map> person = repository.selectPersonsMap();
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  void selectPersonsSet() {
    Set<Map> person = repository.selectPersonsSet();
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  void selectListString() {
    List<String> firstPersonName = repository.selectListString();
    System.out.println(firstPersonName);
  }

  @Test
  void selectInt() {
    int i = repository.selectInt();
    System.out.println(i);
  }

  @Test
  void selectString() {
    String str = repository.selectString();
    System.out.println(str);
  }

  @Test
  void selectV() {
    Person str = repository.selectV();
    System.out.println(str);
  }


  @Test
  void selectListV() {
    List<Person> str = repository.selectListV();
    System.out.println(str);
  }


  @Test
  void selectStringParam() {
    String name = repository.selectStringParam("经由数据库传输中文");
    System.out.println(name);
  }

  @Test
  void selectIntParam() {
    Integer name = repository.selectIntParam(12);
    System.out.println(name);
  }

  @Test
  void selectBoolParam() {
    Boolean name = repository.selectBoolParam(false);
    System.out.println(name);
  }


  @Test
  void selectCustomPage() {
    Page<Person> page = new Page<>();
    page.setPageSize(3);
    page.setPageNo(1);
    List<Person> name = repository.selectCustomPage(page);
    System.out.println(JSON.toJSONString(name));
    System.out.println(JSON.toJSONString(page));
  }

  @Test
  void selectCustomPageAndName() {
    Page<Person> page = new Page<>();
    page.setPageSize(3);
    page.setPageNo(1);
    List<Person> name = repository.selectCustomPageAndName(page, "丁小碧");
    System.out.println(JSON.toJSONString(name));
    System.out.println(JSON.toJSONString(page));
  }


  @Test
  public void selectPersonLikePerson() {
    List<PersonLikePerson> nrn2s = repository.selectPersonLikePerson();
    System.out.println(JSON.toJSONString(nrn2s));
  }

  @Test
  public void selectPersonLikePersonLimit1() {
    PersonLikePerson nrn2 = repository.selectPersonLikePersonLimit1();
    System.out.println(JSON.toJSONString(nrn2));
  }

  @Test
  public void testMulti() {
    ResultSet resultSet = repository.testMulti();
    System.out.println(resultSet);
  }

  @Test
  public void testStringPropNull_insert() {
    Person person = new Person();
    String genderNullTest = "genderNullTest";
    person.setName(genderNullTest);
    repository.insert(person);
    Person personDb = repository.selectById(genderNullTest);
    System.out.println(JSON.toJSONString(personDb));
    assert personDb.getGender() == null;
  }
  
  @Test
  public void selectMapWhenNull() {
    Map<String, Object> result = repository.selectMapWhenNull();
    Assert.isTrue(result == null);;
  }

  @Test
  public void testSpaceSwitch() {
    for (int i = 0; i < 30; i++) {
      long l = System.currentTimeMillis();
      int mod = (int) (l % 3);
      switch (mod) {
        case 0: repository.testSpaceSwitchStep1();
          break;
        case 1: repository.testSpaceSwitchStep2();
          break;
        case 2: repository.selectMapWhenNull();
          break;
        default: break;
      }
    }
  }
  
  @Test
  public void insertWithTimestamp() {
    Person person = new Person();
    person.setAge(null);
    person.setName("赵小洋");
    person.setBirthday(new Timestamp(System.currentTimeMillis()));
    repository.insertWithTimestamp(person);
  }

}
