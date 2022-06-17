// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.Env;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

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
    public void pkGeneratorTest() {
        Person person = new Person();
        repository.insertSelective(person);
        System.out.println(JSON.toJSONString( person ) );
    }



}
