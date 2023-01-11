
<!--
Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.

This source code is licensed under Apache 2.0 License.
-->

# 1.1.2-SNAPSHOT
- fix: `ng.join` bug [#122](https://github.com/nebula-contrib/ngbatis/issues/122)
- feat: provide default data structure for edge \ vertex \ path \ sub-graph, and their result handler. #103 #118
- feat: NebulaDaoBasic shortest path support. #118
- feat: ng.valueFmt support escape ( default true ). Use `ValueFmtFn.setEscape( false );` to disable this feature.

# 1.1.1
- fixed #89 BigDecimal / Set / Collection serialization to NebulaValue #97

# 1.1.0
## Features
- springcloud+nacos support #55 
- add upsert tag/edge function #82 
- support #39, use @javax.persistence.Transient #43

## Enhancement
- enhanced: #64 `debug log` print current space in session before switch #79 
- enhanced: NebulaDaoBasic default impls can be overwritten by xml #76 
- optimize #69 display exception detail & enable NebulaDaoBasic to support space switching #70 
- docs typo #52 

## Bugfix
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
