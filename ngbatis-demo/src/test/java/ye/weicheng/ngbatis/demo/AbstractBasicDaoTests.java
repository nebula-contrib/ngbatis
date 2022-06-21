// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Like;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

import java.util.List;

/**
 * @author yeweicheng
 * @since 2022-06-13 0:47
 * <br>Now is history!
 */
@SpringBootTest
public class AbstractBasicDaoTests {
    @Autowired
    private TestRepository repository;

    @Test
    public void insert() {
        Person person = new Person();
        person.setAge( 18 );
        person.setName( "取名有点难");
        repository.insert(person);
    }
    @Test
    public void insertSelective() {
        Person person = new Person();
//        person.setAge( 18L );
        person.setName( "取名有点难2");
        repository.insertSelective(person);
    }

    @Test
    public void selectById() {
        String id = "取名有点难2";
        Person person = repository.selectById(id);
        System.out.println( JSON.toJSONString( person ) );
    }

    @Test
    public void pkGeneratorTest() {
        Person person = new Person();
        repository.insertSelective(person);
        System.out.println(JSON.toJSONString( person ) );
    }

    @Test
    public void selectBySelective() {
        Person person = new Person();
        person.setName( "取名有点难2" );
        person.setAge( 18 );
        List<Person> people = repository.selectBySelective(person);
        System.out.println(JSON.toJSONString( people ) );
    }

    @Test
    public void deleteLogicById () {
        int row = repository.deleteLogicById("取名有点难2");
        System.out.println( row );
    }

    @Test
    public void insertEdge() {
        Person person1 = new Person();
        person1.setName( "测试" );
        repository.insertSelective( person1 );

        Person person2 = new Person();
        repository.insertSelective( person2 );

        Like like = new Like();

        repository.insertEdge( person1, like , person2 );
    }

    @Test
    public void insertEdgeWithProps() {
        Person person1 = new Person();
        person1.setName( "edgeDemoV1" );
        repository.insertSelective( person1 );

        Person person2 = new Person();
        person2.setName( "edgeDemoV2" );
        repository.insertSelective( person2 );

        Like like = new Like();
        like.setLikeness( 0.7 );

        repository.insertEdge( person1, like , person2 );
    }



}
