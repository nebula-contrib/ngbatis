package ye.weicheng.ngbatis.demo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.nebula.contrib.ngbatis.exception.QueryException;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.pojo.PersonLikePerson;
import ye.weicheng.ngbatis.demo.repository.TestRepository;
import ye.weicheng.ngbatis.demo.repository.TestRepository.DynamicNode;

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
  public void selectVertexes() {
    List<NgVertex<String>> ngVertices = repository.selectVertexes();
    System.out.println(JSON.toJSONString(ngVertices));
  }

  @Test
  public void selectEdges() {
    List<NgEdge<String>> ngEdges = repository.selectEdges();
    System.out.println(JSON.toJSONString(ngEdges));
  }

  @Test
  public void selectSubgraph() {
    List<NgSubgraph<String>> ngSubgraphs = repository.selectSubgraph();
    System.out.println(JSON.toJSONString(ngSubgraphs));
  }

  @Test
  public void selectByPerson() {
    Person p = new Person();
    p.setName("叶小南");
    p.setAge(18);
    List<Person> persons = repository.selectByPerson(p);
    System.out.println(persons);
  }
  
  @Test
  public void insertDynamic() {
    DynamicNode node = new DynamicNode();
    node.setTagName("person");
    node.setPropertyList(new HashMap<String, Object>() {{
        put("age", 18);
      }}
    );
    DynamicNode node2 = new DynamicNode();
    node2.setTagName("person");
    node2.setPropertyList(new HashMap<String, Object>() {{
        put("age", 18);
      }}
    );
    DynamicNode node3 = new DynamicNode();
    node3.setTagName("person");
    node3.setPropertyList(new HashMap<String, Object>() {{
        put("age", 18);
      }}
    );
    List<DynamicNode> nodes = Arrays.asList(node, node2, node3);
    repository.insertDynamic(nodes);
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
  public void spaceFromParam() {
    String spaceName = "test" + System.currentTimeMillis();
    try {
      repository.spaceFromParam(spaceName);
    } catch (Exception e) {
      assertSpaceFailed(e);
    }
  }

  @Test
  public void dynamicSpaceWithPage() {
    String spaceName = "test" + System.currentTimeMillis();
    try {
      Page<Person> page = new Page<>();
      page.setPageSize(3);
      page.setPageNo(1);
      repository.dynamicSpaceWithPage(page, spaceName);
    } catch (Exception e) {
      assertSpaceFailed(e);
    }
  }

  void assertSpaceFailed(Exception e) {
    e.printStackTrace();
    String message = e.getMessage();
    Assert.isTrue(e instanceof QueryException
      && (message.contains("SpaceNotFound") || (message.contains("create session failed.")))
    );
  }
  
  @Test
  public void insertWithTimestamp() {
    Person person = new Person();
    person.setAge(null);
    person.setName("赵小洋");
    person.setBirthday(new Date());
    repository.insertWithTimestamp(person);
  }
  
  @Test
  public void testResultContainingSet() {
    NgSubgraph<String> rs = repository.resultContainingSet();
    System.out.println(JSON.toJSONString(rs));
  }

}
