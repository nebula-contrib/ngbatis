# 如何传入参数

通过【[自定义nGQL](./#/?path=dev-example&file=custom-crud)】我们已经知道了如何使自己编写的 `nGQL | cypher` 如何通过 java 调用并执行到 nebula 中。  
接下来，将在当前部分介绍如何对自己编写的语句进行传参。
> **注意**：所有参数基本类型目前只支持包装类，如 int 需要写成 Integer。

## 在 `nGQL | cypher` 中读取参数

### 具名参数
> 参数被 @Param 所注解。org.springframework.data.repository.query.Param
#### 简单类型
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectByName( @Param("name") String param );
    ```

- PersonDao.xml
    ```xml
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $name
        RETURN n
        LIMIT 1
    </select>
    ```

#### POJO 或 Map
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectByName( @Param("person") Person person );
    ```
- PersonDao.xml
    ```xml
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $person.name
        RETURN n
        LIMIT 1
    </select>
    ```
> 参数读取支持 `.` 运算符

### 匿名参数
- 如果在接口中声明的参数中，不带 @Param 注解，则使用下标位进行获取，即 $p0、$p1、$p2、$p3 ...
- 参数列表长度为1，类型为 POJO 或 Map 时，可直接读取属性


#### 基本类型
- PersonDao.java
    ```java
    Person selectByName( String name );
    ```

- PersonDao.xml
    ```xml
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $p0
        RETURN n
        LIMIT 1
    </select>
    ```

#### POJO 或 Map
##### 参数只有一个时：
- PersonDao.java
    ```java
        Person selectByName( Person person );
    ```
- PersonDao.xml
    ```xml
    <select id="selectByName">
        MATCH (n: person)
        WHERE n.person.name == $name
        RETURN n
        LIMIT 1
    </select>
    ```

##### 参数有两个及以上时：
- PersonDao.java
    ```java
        // params = { age: 18 }
        Person selectByName( Person person, Map<String, Object> params );
    ```
- PersonDao.xml
    ```xml
        <select id="selectByName">
            MATCH (n: person)
            WHERE n.person.name == $p0.name
            AND n.person.age > $p1.age
            RETURN n
            LIMIT 1
        </select>
    ```

### 集合类型的参数获取，与基本类型一致。
- 匿名时，使用 $p0，$p1，...
- 具名时，直接使用注解内的参数名
- 如果是基本类型的集合，不需要做复杂处理，可以直接传入
---
- PersonDao.java
    ```java
        List<Person> findByIds( List<String> names );
    ```
- PersonDao.xml
    ```xml
        <select id="findByIds" resultType="your.domain.Person">
            MATCH (n: person)
            WHERE id(n) in $p0
            RETURN n
        </select>
    ```

## 总结
到此，关于参数获取的大致介绍完毕。如果有关于获取参数后的条件控制以及遍历的需求，请移步【[参数条件控制](./#/?path=dev-example&file=parameter-if)】、【[参数遍历](./#/?path=dev-example&file=parameter-for)】  
