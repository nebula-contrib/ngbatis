
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# [TODO]

## Features

- [ ] Expand the function of NebulaDaoBasic
  - [ ] Add batch interface:
    - [ ] insertTripletBatch
    - [ ] insertEdgeBatch
    - [ ] ...
  - [ ] Schema support:
    - [ ] show metas
    - [ ] create | alter tag & edge type
    - [ ] index
- [ ] ResultSetUtil more column types support
  - [ ] Geography
  - [x] Duration

## Dependencies upgrade

- [ ] Springboot 3.x support.

# NEXT

## Dependencies upgrade

- nebula-java: 3.5.0 -> 3.6.0

## Bugfix

- fix: [#190](https://github.com/nebula-contrib/ngbatis/issues/190) Insert failed when tag has no attributes
- chore: removing and exclude some packages: log4j related or useless.
- fix: [#194](https://github.com/nebula-contrib/ngbatis/issues/194) we can name the interface by `@Component` and `@Resource`, for example:
  - `@Component("namedMapper")`: use `@Resource("namedMapper$Proxy")` to inject. (since v1.0)
  - `@Resource("namedComponent")`: use `@Resource("namedComponent")` to inject. (new feature)
- fix: when DAO/Mapper method has `Page` type param with `@Param`, the param name can not be use.
  > 如原来项目中分页相关接口，用了不起作用的 `@Param`, 但 xml 还是使用 p0, p1...
  > 需要将 `@Param` 移除，或者将 xml 中的参数名改成 注解的参数名，以保证参数名统一

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
    ```

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
