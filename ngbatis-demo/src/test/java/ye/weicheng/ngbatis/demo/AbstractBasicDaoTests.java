// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import com.alibaba.fastjson.JSON;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nebula.contrib.ngbatis.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Like;
import ye.weicheng.ngbatis.demo.pojo.LikeWithRank;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**.
 * @author yeweicheng.
 * @since 2022-06-13 0:47 <br>.
 *     Now is history.
.*/
@SpringBootTest
public class AbstractBasicDaoTests {
  @Autowired private TestRepository repository;

  @Test
  public void insert() {
    Person person = new Person();
    person.setAge(18);
    person.setName("赵小洋");
    repository.insert(person);
  }

  @Test
  public void insertSelective() {
    Person person = new Person();
    // person.setAge( 18L );
    person.setName("王小冰");
    repository.insertSelective(person);
  }

  @Test
  public void selectById() {
    String id = "赵小洋";
    Person person = repository.selectById(id);
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  public void pkGeneratorTest() {
    Person person = new Person();
    repository.insertSelective(person);
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  public void selectBySelective() {
    Person person = new Person();
    person.setName("赵小洋");
    person.setAge(18);
    List<Person> people = repository.selectBySelective(person);
    System.out.println(JSON.toJSONString(people));
  }

  @Test
  public void deleteWithEdgeById() {
    int row = repository.deleteWithEdgeById("赵小洋");
    System.out.println(row);
  }

  @Test
  public void insertEdge() {
    Person person1 = new Person();
    person1.setName("赵小洋");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("易小海");
    repository.insertSelective(person2);

    Like like = new Like();

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeWithRank() {
    Person person1 = new Person();
    person1.setName("叶小南");
    repository.insertSelective(person1);

    Person person2 = new Person();
    repository.insertSelective(person2);

    LikeWithRank like = new LikeWithRank();

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeWithProps() {
    Person person1 = new Person();
    person1.setName("吴小极");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("刘小洲");
    repository.insertSelective(person2);

    Like like = new Like();
    like.setLikeness(0.7);

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeWithPropsAndRank() {
    Person person1 = new Person();
    person1.setName("李大印");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("王小雪");
    repository.insertSelective(person2);

    LikeWithRank likeWithRank = new LikeWithRank();
    likeWithRank.setLikeness(0.7);

    repository.insertEdge(person1, likeWithRank, person2);
  }

  @Test
  public void insertSelectiveWithDate() {
    Person person = new Person();
    person.setBirthday(new Date());
    person.setName("丁小碧");
    repository.insertSelective(person);
  }

  @Test
  public void selectPage() {
    Page<Person> page = new Page<>();
    page.setPageNo(1);
    page.setPageSize(30);
    Person entity = new Person();
    // entity.setName( "1655802721996" );
    entity.setAge(18);
    page.entity = entity;
    repository.selectPage(page);
    System.out.println(JSON.toJSONString(page));
  }

  @Test
  public void existsEdge() {
    Boolean existsEdge = repository.existsEdge("李大印", Like.class, "丁小碧");
    System.out.println(existsEdge);
  }

  @Test
  public void listStartNodes() {
    List<Person> personList = repository.listStartNodes(Like.class, "易小海");
    System.out.println(JSON.toJSONString(personList));
  }

  @Test
  public void startNode() {
    Person whoIsStartForTest = repository.startNode(Like.class, "易小海");
    System.out.println(JSON.toJSONString(whoIsStartForTest));
  }
}
