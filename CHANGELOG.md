
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# [TODO]

## Features

- [ ] Expand the function of NebulaDaoBasic
  - [ ] Add batch interface:
    - [ ] insertTripletBatch
    - [x] insertEdgeBatch
    - [ ] ...
  - [ ] Schema support:
    - [ ] show metas
    - [ ] create | alter tag & edge type
    - [ ] index
- [ ] ResultSetUtil more column types support
  - [ ] Geography
  - [x] Duration

## Dependencies upgrade

- [x] Springboot 3.x support. (lastest-jdk17)

# NEXT

# 1.3.0

## Dependencies upgrade

- nebula-java: 3.6.0 -> 3.8.3
- org.hibernate:hibernate-core was excluded.
  > If you need to use hibernate-core, please add the dependency by yourself.

## Bugfix

- fix: when `use-session-pool` and spaceFromParam is true, skip the space addition.
- fix: when timezone is not default, the time is incorrect.
- fix: allow normal startup without any mapper files.
- fix: Limit the node type obtained by `selectById` to the entity class of the interface.
- fix: When a node has multiple tags, prioritize using the tag of `resultType`. (Collaborate with [charle004](https://github.com/charle004), [#311](https://github.com/nebula-contrib/ngbatis/pull/311))
- fix: debugging log output issue [#312](https://github.com/nebula-contrib/ngbatis/issues/312)

## Feature

- feat: support the use of ciphertext passwords in yml.
- feat: expanding the `insertSelectiveBatch` interface in `NebulaDaoBasic`.([#299](https://github.com/nebula-contrib/ngbatis/pull/299), via [Ozjq](https://github.com/Ozjq))
- feat: expanding the `shortestOptionalPath` interface in `NebulaDaoBasic`.([#303](https://github.com/nebula-contrib/ngbatis/pull/303), via [xYLiu](https://github.com/n3A87))
- feat: expanding the `showSpaces` interface in `NebulaDaoBasic`.([#304](https://github.com/nebula-contrib/ngbatis/pull/304), via [xYLiu](https://github.com/n3A87))
- feat: support ssl and http2 config in yml.
  > http2 属于企业版的数据库才支持，但我没有测试环境，所以不确定是否可用。
  > http2 is supported by the enterprise version of the database, but I don't have a test environment, so I'm not sure if it works.
- feat: support adding ID attributes of start and end nodes in edge entities.
  > 通过 @DstId, @SrcId 进行注解，可以将属性标记成特殊的属性，用于查询时可以填充的起始点和终点的id值。

  - example:

    ```yaml
    nebula:
      pool-config:
        enable-ssl: true
        ssl-param:
          sign-mode: SELF_SIGNED
          crt-file-path: /path/to/client.crt
          key-file-path: /path/to/client.key
          password: password
        # ssl-param:
          # sign-mode: CA_SIGNED
          # ca-crt-file-path: /path/to/ca-client.crt
          # crt-file-path: /path/to/client.crt
          # key-file-path: /path/to/client.key
        use-http2: false
        custom-headers:
          Route-Tag: abc
    ```

- feat: @Space annotation supports dynamic configuration.
  > @Space 注解的 name 属性值可通过 spring 配置文件自定义配置。
  - example:

    ```yaml
    app:
      person:
        space: PERSON_SPACE
    ```
  
    ```java
    @Space(name = "${nebula.space}")
    @Table(name = "person")
    public class Person {
        @Id
        private String vid;
        private String name;
  
        public String getVid() {
            return vid;
        }
  
        public void setVid(String vid) {
            this.vid = vid;
        }
  
        public String getName() {
            return name;
        }
  
        public void setName(String name) {
            this.name = name;
        }
    }
    ```

# 1.2.2

## Bugfix

- fix: complete the error code of ResultSet into QueryException.
- fix: the issue of not being able to handle Set type.
- fix: the issue of the ranking value of the edge object cannot be filled.
- fix: when the field is aliased by `@Column`, the param name is incorrect. (multi tags support)
  - selectBySelective
  - selectIdBySelective
  - selectBySelectiveStringLike
  - selectIdBySelectiveStringLike
- fix: unable to read the correct value of id, the value of the subclass' id is used now. (multi tag scene)
- fix: do not generate asg debug when the log level is not debug, now.

## Develop behavior change

- No longer verifying the number of `@Id` in the entity, please keep the number to 1 on your own.
  > 不再对实体中的`@Id`个数进行校验，请注意保持个数为1 （含父类）

# 1.2.1

## Bugfix

- fix: set the specified space in the original method to the proxy method for paging. ([#282](https://github.com/nebula-contrib/ngbatis/pull/282))

# 1.2.0

## Dependencies upgrade

- Upgrade to `JDK 17` and SpringBoot `3.x`
- nebula-java: 3.5.0 -> 3.6.0
- beetl: 3.1.8-RELEASE -> 3.15.10.RELEASE
- antlr4: 4.7.2 -> 4.11.1
- asm: 8.0 -> 9.4
- jsoup: 1.15.3 -> 1.15.4

## Feature

- feat: support `<nGQL>` include query pieces. ([#212](https://github.com/nebula-contrib/ngbatis/pull/212), via [dieyi](https://github.com/1244453393))
- feat: extending `NgPath`, when 'with prop' is used in nGQL, edge attributes can be obtained from NgPath. ([#228](https://github.com/nebula-contrib/ngbatis/pull/228), via [dieyi](https://github.com/1244453393))
- feat: expanding the `insertEdgeBatch` interface in `NebulaDaoBasic`. ([#244](https://github.com/nebula-contrib/ngbatis/pull/244), via [Sunhb](https://github.com/shbone))
- feat: expanding the `deleteByIdBatch` interface in `NebulaDaoBasic`. ([#247](https://github.com/nebula-contrib/ngbatis/pull/247), via [Sunhb](https://github.com/shbone))
- feat: expanding the `listEndNodes` interface in `NebulaDaoBasic`. ([#272](https://github.com/nebula-contrib/ngbatis/pull/272), via [knqiufan](https://github.com/knqiufan))
- feat: support specify space by param

## Bugfix

- fix: support methods in mapper tags to set space to null.
  - Such as:

  ```xml
  <mapper namespace="...">
    <create id="createSpace" space="null">
      create space new_space ( vid_type  = INT64 );
    </create>
  </mapper>
  ```

- fix: [#190](https://github.com/nebula-contrib/ngbatis/issues/190) Insert failed when tag has no attributes
- chore: removing and exclude some packages: log4j related or useless.
- fix: [#194](https://github.com/nebula-contrib/ngbatis/issues/194) we can name the interface by `@Component` and `@Resource`, for example:
  - `@Component("namedMapper")`: use `@Resource("namedMapper$Proxy")` to inject. (since v1.0)
  - `@Resource("namedComponent")`: use `@Resource("namedComponent")` to inject. (new feature)
- fix: when DAO/Mapper method has `Page` type param with `@Param`, the param name can not be use.
  > 如原来项目中分页相关接口，用了不起作用的 `@Param`, 但 xml 还是使用 p0, p1...
  > 需要将 `@Param` 移除，或者将 xml 中的参数名改成 注解的参数名，以保证参数名统一
- fix:class 'ResultSetUtil.java' parse datetime type error. ([#241](https://github.com/nebula-contrib/ngbatis/pull/241), via [爱吃辣条的Jerry](https://github.com/bobobod))
- fix: remove unnecessary reflections in transformDateTime, and prevents errors in the millisecond bit in jdk17.

## Develop behavior change

- Remove deprecated classes and methods:
  - org.nebula.contrib.ngbatis.binding.DateDeserializer
  - org.nebula.contrib.ngbatis.binding.DefaultArgsResolver#customToJson
- Dependencies changing:
  > 如果项目中有用到，且出现相关类找不到的情况，请自行引入
  - Exclude:

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-to-slf4j</artifactId>
            </exclusion>
            <exclusion>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-api</artifactId>
            </exclusion>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    ```
  
  - Removing:

    ```xml
    <!-- Why: make it possible to use undertow as web server -->
    <dependency> 
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- Why: useless in NgBatis-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

# 1.1.5

## 1.1.5 Bugfix

- fix: [#176](https://github.com/nebula-contrib/ngbatis/issues/176) use double quote instead of the original single quote in valuaFmt function
- fix: [#181](https://github.com/nebula-contrib/ngbatis/issues/181) when node has multi tag, can not update by subclass
  - updateById
  - updateByIdSelective
  - updateByIdBatchSelective
  - updateByIdBatchSelective
  - upsertByIdSelective
- fix: [#185](https://github.com/nebula-contrib/ngbatis/issues/185) improve the accuracy of datetime to milliseconds

# 1.1.4

## 1.1.4 Behavior Changes

- When a field is declared by java.util.Date, it is no longer allowed to set a value using Timestamp
  > 当字段由java.util.Date声明时，不再允许使用java.sql.Timestamp设值

## 1.1.4 Bugfix

- fix: data error for date type in the database.[#102](https://github.com/nebula-contrib/ngbatis/issues/102)

## Feature

- Clear time type mapping.

  db type | java type
  ---|---
  datetime | java.util.Date
  date | java.sql.Date
  time | java.sql.Time
  timestamp | java.sql.Timestamp
  duration | java.time.Duration

# 1.1.3

## 1.1.3 Bugfix

- fix: make the error message clearer when 'use space' failed [#150](https://github.com/nebula-contrib/ngbatis/issues/150)
- fix: sessionPool is null when the space name only declared in yml

## 1.1.3 Dependencies upgrade

- nebula-java: 3.4.0 -> 3.5.0

# 1.1.2

## 1.1.2 Behavior Changes

- If an entity type is another entity type's super class, all attribute are being required in database schema except `@Transient`
    > 如果一个实体类是另一个实体类的父类，则其所有除了注解`@Transient` 了的属性，都需要在数据库中声明。

## 1.1.2 Bugfix

- fix: when vertex has multi tags cannot set value properly.[#120](https://github.com/nebula-contrib/ngbatis/issues/120)
- fix: `ng.join` bug [#122](https://github.com/nebula-contrib/ngbatis/issues/122)

## 1.1.2 Features

- feat: multi tags support for vertex inserting.
- feat: provide default data structure for edge \ vertex \ path \ sub-graph, and their result handler. #103 #118
- feat: NebulaDaoBasic shortest path support. #118
- feat: ng.valueFmt support escape ( default true ). Use `ValueFmtFn.setEscape( false );` to disable this feature.
- feat: add config to use `nebula-java` session pool

  ```yaml
  nebula:
    ngbatis:
      use-session-pool: true
  ```

## 1.1.2 Dependencies upgrade

- nebula-java: 3.3.0 -> 3.4.0

# 1.1.1

- fixed #89 BigDecimal / Set / Collection serialization to NebulaValue #97

# 1.1.0

## 1.1.1 Features

- springcloud+nacos support #55
- add upsert tag/edge function #82
- support #39, use @javax.persistence.Transient #43

## Enhancement

- enhanced: #64 `debug log` print current space in session before switch #79
- enhanced: NebulaDaoBasic default impls can be overwritten by xml #76
- optimize #69 display exception detail & enable NebulaDaoBasic to support space switching #70
- docs typo #52

## 1.1.1 Bugfix

- fixed #89 splitting param serialization into two forms, json and NebulaValue #92
- fixed #78 use space and gql are executed together incorrect in 3.3.0 #87
- fixed #73 `selectById` use id value embedding instead of cypher parameter #74
- fixed #65 `selectByIds` use id values embedding instead of cypher param #67
- fixed the error of "ng.id" when id is in super class #62
- fixed #51 The node params support the direct use of the ID value when insert edge #60
- fixed #56 make it work well when returnType is Map and result is null #58
- fixed #47 console bug when result type is basic type #48

# 1.1.0-rc

增加了一些内置函数可以在 xml 中使用：

- ng.valueFmt
  > 对不定类型的数据值进行格式化，忽略是否追加单引号及日期格式化，直接传原始 java类型即可

  参数位 | 参数说明 | 类型  | 是否必传 | 默认值
  ---|---|---|---|---
  1 | 值 | Object | Y |
  2 | 如果是字符串是否在前后追加 .* 形成模糊查询 | boolean | N | false

- ng.schemaFmt
  > 对模式名前后追加 **`**，以避免与数据库关键字冲突

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 模式名，如 tagName, edgeName, propertyName | Object | Y

- ng.tagName
  > 用于从实体类或Dao接口获取 tag name

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 与Schema对应的实体类对象 | Object | Y
  2 | 类模型，使用 `ng_cm` 传入 | ClassModel | N | null

- ng.pkField
  > 用于获取 主键属性，java.lang.reflect.Field

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类类型 | Class<?> | Y
  2 | 如果不存在主键是否报错中断 | Boolean | N | false

- ng.pkName
  > 用于获取 主键名，String

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类对象 | Object | Y
  2 | true 时使用列名，false 时使用属性名 | Boolean | N | true

- ng.entityType

  > 用于获取实体类类型

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类对象 | Object | Y

- ng.fieldNames
  > 获取属性名集合（不包括主键）

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类对象 | Object | Y
  2 | true 时使用列名，false 时使用属性名 | Boolean | N | true
  
- ng.id
  > 获取id值

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类对象 | Object | Y
  2 | 如果不存在主键是否报错中断 | Boolean | N | true
  3 | 如果值为空，true会通过主键生成器返回新值，false 时 返回空 | Boolean | N | true
  
- ng.kv
  > 通过实体对象或者获取多个集合
  >
  > - columns 列名集合
  > - valueNames 属性名集合
  > - values 值集合
  > - types 属性类型

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 实体类对象 | Object | Y
  2 | 参数名前缀 | String | N | null
  3 | 是否排除主键 | Boolean | N | true
  4 | 是否排除空值 | Boolean | N | true
  5 | 如无主键，是否报错中断 | Boolean | N | true
  
- ng.join
  > 对集合进行格式化

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 待格式化的集合 | Iterable | Y
  2 | 元素间的分隔符 | String | N | `,`
  3 | 函数名，各元素拼接前，可进行函数名指定的格式化函数先行格式化，再拼接 | String | N | null

- ng.ifStringLike
  > 类型为字符串时，前后拼接 `.*`

  参数位 | 参数说明 | 类型 | 必传 | 默认值
  ---|---|---|---|---
  1 | 值 | Object | Y
  2 | 属性类型 | Object | N | null
  3 | 属性名，用于不将值明文写在 ngql 中，而使用参数名，让 nebula 在参数中取值 | String | N | null

# 1.1.0-beta
