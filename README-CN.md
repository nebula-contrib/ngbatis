
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# NGBATIS

<p align="center">
  <br> <a href="README.md">English</a> | 中文
</p>

- [Ngbatis Docs](https://corvusye.github.io/ngbatis-docs/#/)

## NGBATIS是什么？

**NGBATIS** 是一款针对 [Nebula Graph](https://github.com/vesoft-inc/nebula) + Springboot 的数据库 ORM 框架。借鉴于 [MyBatis](https://github.com/mybatis/mybatis-3) 的使用习惯进行开发。包含了一些类似于[mybatis-plus](https://github.com/baomidou/mybatis-plus)的单表操作，另外还有一些图特有的实体-关系基本操作。  
如果使用上更习惯于JPA的方式，[graph-ocean](https://github.com/nebula-contrib/graph-ocean) 是个不错的选择。

## NGBATIS 是怎么运行的？请看设计文档 [EXECUTION-PROCESS.md](./EXECUTION-PROCESS.md)

## 项目要求
- Springboot
- Maven
- Java 8+

## 如何使用（可在克隆代码后，参考 ngbatis-demo 项目）
### 在项目引入
```xml
    <dependency>
      <groupId>org.nebula-contrib</groupId>
      <artifactId>ngbatis</artifactId>
      <version>1.1.1</version>
    </dependency>
```
### 参考 [【ngbatis-demo】](./ngbatis-demo)，与springboot无缝集成。在该项目的 test 中还有api的样例。在开发过程中每增加一个特性也都会同步更新ngbatis-demo的用例。


### 配置数据库
在 application.yml 中添加配置 **将数据源修改成可访问到的NebulaGraph**
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
### 扫描动态代理的 bean
```java
@SpringBootApplication(scanBasePackages = { "ye.weicheng", "org.nebula" })
public class YourApplication {
    public static void main(String[] args) {
        new SpringApplication(YourApplication.class).run(args);
    }
}
```

## 日常开发示例
### 自己编写 nGQL (MyBatis的思路)
#### 声明数据访问接口
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
#### 编写数据访问语句
resource/mapper/TestRepository.xml
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

### 使用基类自带的 nGQL 实现图的基本操作（MyBatis-plus）的思路

#### model-vertex
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
#### model-edge
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

#### dao
```java
package com.example.dao;

import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import com.example.model.vertex.Person;

public interface PersonDao extends NebulaDaoBasic<Person, String>{}
```
#### xml（不可缺少）
```xml
<mapper
    namespace=
    "com.example.dao.PersonDao"
>
</mapper>
```
#### service
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

## License
This code is under the [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0).