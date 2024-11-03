
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# NgBatis

<p align="center">
  <br> <a href="README.md">English</a> | 中文
</p>

- [NgBatis Docs](https://nebula-contrib.github.io/ngbatis/)
- [NgBatis 文档](https://graph-cn.github.io/ngbatis-docs/)

## NGBATIS是什么？

**NgBatis** 是一款针对 [Nebula Graph](https://github.com/vesoft-inc/nebula) + Springboot 的数据库 ORM 框架。借鉴于 [MyBatis](https://github.com/mybatis/mybatis-3) 的使用习惯进行开发。包含了一些类似于[mybatis-plus](https://github.com/baomidou/mybatis-plus)的单表操作，另外还有一些图特有的实体-关系基本操作。  
如果使用上更习惯于JPA的方式，[graph-ocean](https://github.com/nebula-contrib/graph-ocean) 是个不错的选择。

## NgBatis 是怎么运行的？

请看设计文档 [EXECUTION-PROCESS.md](./EXECUTION-PROCESS.md)

## 项目要求

- Springboot
- Maven
- Java 8+

## 版本匹配

  NgBatis | nebula-java | JDK | Springboot | Beetl
  ---|-------------|---|------------|---
  1.3.0 | 3.8.3       | 8 | 2.7.0 | 3.15.10.RELEASE
  1.3.0-jdk17 | 3.8.3       | 17 | 3.0.7 | 3.15.10.RELEASE
  1.2.2 | 3.6.0       | 8 | 2.7.0 | 3.15.10.RELEASE
  1.2.2-jdk17 | 3.6.0       | 17 | 3.0.7 | 3.15.10.RELEASE
  1.2.1 | 3.6.0       | 8 | 2.7.0 | 3.15.10.RELEASE
  1.2.0-jdk17 | 3.6.0       | 17 | 3.0.7 | 3.15.10.RELEASE
  1.2.0 | 3.6.0       | 8 | 2.7.0 | 3.15.10.RELEASE
  1.1.5 | 3.5.0       | 8 | 2.7.0 | 3.1.8.RELEASE
  1.1.4 | 3.5.0       | 8 | 2.7.0 | 3.1.8.RELEASE
  1.1.3 | 3.5.0       | 8 | 2.7.0 | 3.1.8.RELEASE
  1.1.2 | 3.4.0       | 8 | 2.7.0 | 3.1.8.RELEASE

### 快照版

  NgBatis | nebula-java | JDK | Springboot | Beetl
  ---|-------------|---|------------|---
  1.2.2-jdk17-SNAPSHOT | 3.6.0       | 17 | 3.0.7 | 3.15.10.RELEASE
  1.2.2-SNAPSHOT | 3.6.0       | 8 | 2.7.0 | 3.15.10.RELEASE

> 在同版本号快照版中，依赖的第三方可能会随时升级

## 如何使用（可在克隆代码后，参考 ngbatis-demo 项目）

### 在项目引入

- Maven

    ```xml
        <dependency>
          <groupId>org.nebula-contrib</groupId>
          <artifactId>ngbatis</artifactId>
          <version>1.3.0</version>
        </dependency>
    ```

- Gradle

    ```groovy
    implementation 'org.nebula-contrib:ngbatis:1.3.0'
    ```

### 参考 [【ngbatis-demo】](./ngbatis-demo)，与springboot无缝集成。在该项目的 test 中还有api的样例。在开发过程中每增加一个特性也都会同步更新ngbatis-demo的用例

### 配置数据库

在 application.yml 中添加配置 **将数据源修改成可访问到的NebulaGraph**

```yml
nebula:
  ngbatis:
    session-life-length: 300000 # since v1.1.2
    check-fixed-rate: 300000 # since v1.1.2
    # `use-session-pool` 默认是 false，如开启，则 space 是必须指定的
    # space除了使用当前文件的声明意外，还可以使用：(@Space) or xml(space="test")
    use-session-pool: false # since v1.1.2
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
@SpringBootApplication(scanBasePackages = { "org.nebula", "your.domain"})
public class YourApplication {
    public static void main(String[] args) {
        new SpringApplication(YourApplication.class).run(args);
    }
}
```

> 如果项目中使用的是 SpringCloud，
> 请使用`@ComponentScan( basePackages = {"org.nebula.contrib", "your.domain"} )`

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
    // new features from v1.2.0
    Integer returnAge(@Param("person")Person person);
  
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
    <!-- v1.2.0 新特性 start -->
    <nGQL id="include-test-value">
        ${myInt}
    </nGQL>

    <nGQL id="ngql-return-age">
        RETURN @ng.include('include-test-value',{'myInt':age});
    </nGQL>

    <!--
    等同于: 
        RETURN ${person.age};
    你可以试着提取更多有意义的公共脚本.
    -->
    <select id="returnAge" resultType="java.lang.Integer">
        @ng.include('ngql-return-age',person);
    </select>
    <!-- v1.2.0 新特性 v1.2.0 end -->

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

### 使用提供的方法进行实体直查（OGM）

该查询方式是从实体对象出发完成数据直查。使用前要定义实体类，作为查询参数。

#### 实体类

##### 点实体

- 继承`GraphBaseVertex`类标识是点实体
- `@Tag`的name属性注明点实体的Tag
- `@GraphId`的type属性注明点实体id的类型（可选）

```java
@Tag(name = "player")
public class Player extends GraphBaseVertex {

  @GraphId(type = IdType.STRING)
  private String id;

  private String name;

  private Integer age;
    
  ...

}
```

具体可参考`ye.weicheng.ngbatis.demo.pojo.edge`包下的点实体示例。

##### 边实体

- 继承`GraphBaseEdge`类标识是边实体
- `@EdgeType`的name属性注明边实体的类型
- `@Id`（可选，如果两个节点之间同一类型边的唯一性由源节点id和目标节点id共同决定，可以不加当前属性）
- `@SrcId`（可选，如果不需要获取关系的源节点id，可以不加当前属性）
- `@DstId`（可选，如果不需要获取关系的目标节点id，可以不加当前属性）

```java
@EdgeType(name = "serve")
public class Serve extends GraphBaseEdge {

  @Id 
  private Long rank;

  @SrcId 
  private String srcId;

  @DstId 
  private String dstId;

  @Column(name = "start_year")
  private Integer startYear;
  @Column(name = "end_year")
  private Integer endYear;

  ...
}
```

具体可参考`ye.weicheng.ngbatis.demo.pojo.vertex`包下的边实体示例。

#### 现提供的方法

##### 关于点实体

| API                                                          | 用法说明                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| queryIdsByProperties()                                       | 查询特定Tag或者属性的点Id集合                                |
| queryVertexById()                                            | 查询特定点Id的单个点                                         |
| queryVertexByTag()                                           | 查询特定Tag的点集合                                          |
| queryVertexByProperties()                                    | 查询特定属性的点集合                                         |
| queryAllAdjacentVertex(Class<?>... edgeClass)                | 查询特定点的所有邻点集合，可指定一个或多个连接两点的边类型   |
| queryIncomingAdjacentVertex(Class<?>... edgeClass)           | 查询特定点入边方向的邻点集合，可指定一个或多个连接两点的边类型 |
| queryOutgoingAdjacentVertex(Class<?>... edgeClass)           | 查询特定点出边方向的邻点集合，可指定一个或多个连接两点的边类型 |
| queryNeighborIdsWithHopById(int m, int n, Class<?>... edgeClass) | 查询特定点指定跳数内的点Id集合，可指定一个或多个连接两点的边类型 |
| queryConnectedEdgesById(Direction direction)                 | 查询特定点关联的所有边集合，可指定边的方向和类型             |
| queryPathFromVertex(Direction direction)                     | 查询特定点关联的所有路径集合，可指定边的方向                 |
| queryFixedLengthPathFromVertex(Integer maxHop, Direction direction, Class<?>... edgeClass) | 查询特定点出发的定长路径集合，可指定最大步数、边的方向、边的类型 |
| queryVariableLengthPathFromVertex(Integer minHop, Integer maxHop,   Direction direction, Class<?>... edgeClass) | 查询特定点出发的变长路径集合，可指定最小步数、最大步数、边的方向、边的类型 |
| queryShortestPathFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询特定点出发的任意一条最短路径，可指定步数、边的方向、终点实体 |
| queryAllShortestPathsFromSrcAndDst(Integer maxHop,   Direction direction, T v2) | 查询从该点出发的所有最短路径集合，可指定步数、边的方向、终点实体 |
| queryVertexCountByTag()                                      | 查询特定Tag的点的数量                                        |

具体实现见`org.nebula.contrib.ngbatis.base`包下的点实体基类`GraphBaseVertex`。

##### 关于边实体

| API                                                          | 用法说明                   |
| ------------------------------------------------------------ | -------------------------- |
| queryEdgeByType(Direction direction)                         | 查询特定类型、方向的边集合 |
| queryEdgeWithSrcAndDstByProperties(T srcVertex, Direction direction, T dstVertex) | 查询特定属性的边集合       |
| queryEdgePropertiesBySrcAndDstId()                           | 查询特定始终点id的边集合   |
| queryEdgeCountByType()                                       | 查询特定Type的边的数量     |

具体实现见`org.nebula.contrib.ngbatis.base`包下的边实体基类`GraphBaseEdge`。

#### 使用示例

```java
@Test
public void testVertex(){
    Player srcPlayer = new Player();
    //查询所有符合条件 name = "Vince Carter" 的Player顶点
    srcPlayer.setName("Vince Carter");
    List<Player> vertices = player.queryVertexByProperties();
}

@Test
public void testEdge(){
    Serve serve = new Serve();
    
    //查询起点id为player100，终点id为team204的Serve边
    serve.setSrcId("player100");
    serve.setDstId("team204");
    Serve edge = serve.queryEdgeWithSrcAndDstByProperties();
    
    //查询Serve类型、方向为”->“的边
    List<Serve> edges = serve.queryEdgeByType(Direction.NULL);
    
}
```

具体每个直查方法的使用示例可参考ngbatis-demo里的NebulaGraphBasicTests测试类。

## 特别声明的上游项目

- [beetl](https://gitee.com/xiandafu/beetl), BSD-3, Beetl模板引擎是项目很重要的组成部分(as is).

## 开源协议

项目遵循 [Apache License, Version 2.0, January 2004](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。
