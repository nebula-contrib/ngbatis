# 如何传入参数

通过【[自定义nGQL](./custom-crud)】我们已经知道了如何使自己编写的 `nGQL | cypher` 如何通过 java 调用并执行到 nebula 中。  
接下来，将在当前部分介绍如何对自己编写的语句进行传参。
> **注意**：所有参数基本类型目前只支持包装类，如 int 需要写成 Integer。


## 在 `nGQL | cypher` 中读取参数

## 具名参数
> 参数被 @Param 所注解。org.springframework.data.repository.query.Param
#### 简单类型
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectByName( @Param("name") String param );
    ```
- PersonDao.xml

    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == '${ name }'
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [方式2]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == ${ ng.valueFmt(name) } // 可以忽略类型差异
        RETURN n
        LIMIT 1
    </select>
    ```

    ```xml [参数由 DB 替换]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $name
        RETURN n
        LIMIT 1
    </select>
    ```
    :::

#### POJO 或 Map
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectByName( @Param("person") Person person );
    ```
- PersonDao.xml
    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == '${ person.name }'
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [方式2]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == ${ ng.valueFmt(person.name) }
        RETURN n
        LIMIT 1
    </select>
    ```

    ```xml [参数由 DB 替换]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $person.name
        RETURN n
        LIMIT 1
    </select>
    ```
    :::
> 参数读取支持 `.` 运算符，如果是 POJO 或 Map，可以直接读取内部属性

## 匿名参数
- 如果在接口中声明的参数中，不带 @Param 注解，则使用下标位进行获取，即 ${ p0 }、${ p1 }、${ p2 }、${ p3 } ... 或 $p0、$p1、$p2、$p3 ...
- 参数列表长度为1，类型为 POJO 或 Map 时，可直接读取属性


#### 基本类型
- PersonDao.java
    ```java
    Person selectByName( String name );
    ```

- PersonDao.xml
    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == '${ p0 }'
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [方式2]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == ${ ng.valueFmt(p0) }
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [参数由 DB 替换]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $p0
        RETURN n
        LIMIT 1
    </select>
    ```

    :::
#### POJO 或 Map
##### 参数只有一个时：
- PersonDao.java
    ```java
        Person selectByName( Person person );
    ```
- PersonDao.xml
    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == '${ name }'
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [方式2]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == ${ ng.valueFmt(name) }
        RETURN n
        LIMIT 1
    </select>
    ```
    ```xml [参数由 DB 替换]
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $name
        RETURN n
        LIMIT 1
    </select>
    ```
    :::

##### 参数有两个及以上时：
- PersonDao.java
    ```java
        // params = { age: 18 }
        Person selectByName( Person person, Map<String, Object> params );
    ```
- PersonDao.xml
    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
        <select id="selectByName">
            MATCH (n: person)
            WHERE n.person.name == '${ p0.name }'
            AND n.person.age > ${ p1.age }
            RETURN n
            LIMIT 1
        </select>
    ```
    ```xml [方式2]
        <select id="selectByName">
            MATCH (n: person)
            WHERE n.person.name == ${ ng.valueFmt(p0.name) }
            AND n.person.age > ${ ng.valueFmt(p1.age) }
            RETURN n
            LIMIT 1
        </select>
    ```
    ```xml [参数由 DB 替换]
        <select id="selectByName">
            MATCH (n: person)
            WHERE n.person.name == $p0.name
            AND n.person.age > $p1.age
            RETURN n
            LIMIT 1
        </select>
    ```
    :::

## 集合类型的参数获取，与基本类型一致。
- 匿名时，使用 ${ p0 }、${ p1 }，或 $p0、$p1...
- 具名时，直接使用注解内的参数名
- 如果是基本类型的集合，不需要做复杂处理，可以直接传入
---
- PersonDao.java
    ```java
        List<Person> findByIds( List<String> names );
    ```
- PersonDao.xml
    ::: code-group

    ```xml [参数由 NgBatis 替换，方式1]
        <select id="findByIds" resultType="your.domain.Person">
            MATCH (n: person)
            WHERE id(n) in ${ p0 }
            RETURN n
        </select>
    ```
    ```xml [方式2]
        <select id="findByIds" resultType="your.domain.Person">
            MATCH (n: person)
            WHERE id(n) in ${ ng.valueFmt(p0) }
            RETURN n
        </select>
    ```
    ```xml [参数由 DB 替换]
        <select id="findByIds" resultType="your.domain.Person">
            MATCH (n: person)
            WHERE id(n) in $p0
            RETURN n
        </select>
    ```

    :::


## 常见问题
- 两种占位符格式：
    - `${ 参数名.子参数名 }` ngbatis 的占位符，执行到数据库前完成替换
    -  `$参数名.子参数名` NebulaGraph 的占位符，由数据库完成替换，但需要注意支持参数化的位置。
- 特殊符号问题：
    - 可以使用内置函数进行转义 `${ ng.valueFmt( 参数名 ) }`，该函数同样无需判断数据类型。如是字符串也无需加前后引号

## 总结
到此，关于参数获取的大致介绍完毕。如果有关于获取参数后的条件控制以及遍历的需求，请移步【[参数条件控制](./parameter-if)】、【[参数遍历](./parameter-for)】  
