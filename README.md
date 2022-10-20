
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# NGBATIS

<p align="center">
  <br> English | <a href="README-CN.md">中文</a>
</p>

- [Ngbatis Docs](https://corvusye.github.io/ngbatis-docs/#/)

## What is NGBATIS

**NGBATIS** is a database ORM framework base [NebulaGraph](https://github.com/vesoft-inc/nebula) + spring-boot, which takes advantage of the [mybatis’](https://github.com/mybatis/mybatis-3) fashion development, including some de-factor operations in single table and vertex-edge, like [mybatis-plus](https://github.com/baomidou/mybatis-plus).

If you prefer JPA, [graph-ocean](https://github.com/nebula-contrib/graph-ocean) is a good choice.

## How it works

See [EXECUTION-PROCESS.md](./EXECUTION-PROCESS.md)

## Requirements
- Springboot
- Maven
- Java 8+

## How to use

> You could refer to ngbatis-demo in this repo.

- Include in your `pom.xml`

```xml
    <dependency>
      <groupId>org.nebula-contrib</groupId>
      <artifactId>ngbatis</artifactId>
      <version>1.1.0-rc</version>
    </dependency>
```
- Referring to [ngbatis-demo](./ngbatis-demo), which was smoothly integrated with spring-boot. The API examples could be found under the test of it for all features of ngbatis.

- Configure the NebulaGraph Database

  Configure `application.yml` with the host and credential to enable access to the NebulaGraph Cluster.

```yml
nebula:
  hosts: 127.0.0.1:19669, 127.0.0.1:9669
  username: root
  password: nebula
  space: test
  pool-config:
    min-conns-size: 0
    max-conns-size: 10
    timeout: 0
    idle-time: 0
    interval-idle: -1
    wait-time: 0
    min-cluster-health-rate: 1.0
    enable-ssl: false
```
- Dynamically register beans

```java
@SpringBootApplication(scanBasePackages = { "ye.weicheng", "org.nebula" })
public class YourApplication {
    public static void main(String[] args) {
        new SpringApplication(YourApplication.class).run(args);
    }
}
```

## Examples
### a. The MyBatis fashion(compose nGQL queries)
#### a.1 Declare the data access interface
```java
package ye.weicheng.ngbatis.demo.repository;

import ye.weicheng.ngbatis.demo.pojo.Person;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TestRepository {
    Person selectPerson();
    Person selectByPerson(Person person);
    List<Person> selectAgeGt(Integer age);
    List<String> selectListString();
    List<Map> selectPersonsMap();
    Map<String, Object> selectTriple();
}

```
#### a.2 The query statments
`resource/mapper/TestRepository.xml`

```xml
<mapper
    namespace=
    "ye.weicheng.ngbatis.demo.repository.TestRepository"
>

    <select id="selectPerson" resultType="ye.weicheng.ngbatis.demo.pojo.Person">
        match (v:person) return v.person.name as name, v.person.age as age limit 1
    </select>

    <select id="selectAgeGt" resultType="ye.weicheng.ngbatis.demo.pojo.Person">
        MATCH (n: person)
        WHERE n.person.age > $p0
        RETURN n
        LIMIT 100
    </select>


    <select id="selectByPerson" resultType="ye.weicheng.ngbatis.demo.pojo.Person">
        MATCH (n: person)
        WHERE n.person.name == $p0.name
        RETURN n
        LIMIT 100
    </select>

    <select id="selectListString" resultType="java.lang.String">
        match (v:person) return v.person.name as name limit 100
    </select>

    <select id="selectPersonsMap" resultType="java.util.Map">
        match (v:person) return v.person.name as name, v.person.age  as age limit 100
    </select>

    <select id="selectTriple" resultType="java.util.Map">
        MATCH (n: person)-[r: like]->(n2: person)
        RETURN n, r, n2
        LIMIT 100
    </select>

    <!-- 
        更多复杂的 nGQL 可能还需要充分的测试，
        目前我自己在用的项目两层对象的数据结构也是可以满足的。
        Path 因为开发中基本都可以用 n, r, n2 的结构处理，便还没来得及支持。
    -->

</mapper>
```

### b. The MyBatis-plus fashion

#### b.1 model-vertex
```java
package com.example.model.vertex.Person;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "person")
public class Person {
    @Id
    private String name;
    private Integer age;
}
```
#### b.2 model-edge
```java
package com.example.model.edge.Like;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import javax.persistence.Table;

@Data
@Table(name = "like")
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    private Double likeness;
}
```

#### b.3 dao
```java
package com.example.dao;

import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import com.example.model.vertex.Person;

public interface PersonDao extends NebulaDaoBasic<Person, String>{}
```
#### b.4 xml

> Note, this is a mandatory requirement.

```xml
<mapper
    namespace=
    "com.example.dao.PersonDao"
>
</mapper>
```
#### b.5 service
```java
package com.example.service;

import org.nebula.contrib.ngbatis.utils.Page;
import com.example.dao.PersonDao;
import com.example.model.vertex.Person;
import com.example.model.edge.Like;

@Service
public class PersonServiceImpl {

    @Autowired private PersonDao dao;

    public void demos() {
        // 实现 两个节点插入
        Person tom = new Person();
        tom.setName("Tom");
        dao.insert( tom ); 
        
        Person jerry = new Person();
        jerry.setName( "Jerry" );
        dao.insert( jerry );

        // 建立两个节点的关系
        Like like = new Like( 0.99999 );
        dao.insertEdge( tom, like, jerry );

        // 查找喜欢 jerry 的人
        String jerryId = jerry.getName();
        List<Person> whoLikeJerry = dao.listStartNodes( Like.class, jerryId );

        // 查找唯一喜欢 jerry 的人。非唯一时报错。（限定在特定关系仅有一个上游的场景）
        Person tom = dao.startNode( Like.class, jerryId );

        // 查看 Tom 跟 Jerry 之间的 Like关系
        String tomId = tom.getName();
        Boolean tomLikeJerry = dao.existsEdge( tomId, Like.class, jerryId ); // true
        Boolean jerryLikeTom = dao.existsEdge( jerryId, Like.class, tomId ); // false
        // 可怜的 Tom

        // 根据 Tom 的名字查找全部信息
        Person tomDb = dao.selectById( "Tom" );

        // 查找分页
        Page<Person> page = new Page<>();
        List<Person> personPage = dao.selectPage( page );
        page.getTotal(); // 2 rows， Tom and Jerry
        Boolean theyAreFamily = page.getRows() == personPage; // true

        // 故事总想要有个好的结局
        dao.insertEdge( jerry, like, tom );

        // 更多 基类的操作还在开发中。期待
    }


}

```

## Upstream projects

- [beetl](https://gitee.com/xiandafu/beetl), BSD-3, we proudly use the beetl template language as our template engine, which is consumed in binary package(as is).


## License
This code is under the [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0).
