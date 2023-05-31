# 不同返回值类型
从nebula获得结果后，往往并不能获得业务所需的结果类型，此时，开发者需要告知 ngbatis 具体业务所需的类型。

## 原始类型 [ResultSet](https://github.com/vesoft-inc/nebula-java/blob/master/client/src/main/java/com/vesoft/nebula/client/graph/data/ResultSet.java) (com.vesoft.nebula.client.graph.data.ResultSet)
如果接口返回值为 nebula 的 ResultSet，ngbatis 不做任何处理，直接返回跟调用方，由开发者在调用方中自行处理结果集。
- PersonDao.java
  ```java
    ResultSet returnResultSet();
  ```
- PersonDao.xml
  ```xml
  <select id="returnResultSet">
      RETURN 'You are best'
  </select>
  ```
- > 如果在下列的几种类型中，不能满足开发所需的结果集处理，可以使用获取原始ResultSet类型的返回值，并自行做灵活处理。可阅读[ResultSet源码](https://github.com/vesoft-inc/nebula-java/blob/master/client/src/main/java/com/vesoft/nebula/client/graph/data/ResultSet.java)做近一步了解。

## 非集合类型
此时，结果类型已经通过 方法的返回值类型告知 ngbatis，所以无需额外的配置，即可完成类型映射。
### 基本类型
- PersonDao.java
  ```java
    String returnString();
  ```
- PersonDao.xml
  ```xml
  <select id="returnString">
      RETURN 'You are best'
  </select>
  ```

### POJO | Map
- PersonDao.java
  ```java
    Person returnFirst();
    // 或者 Map returnFirst();
  ```
- PersonDao.xml
  ```xml
  <select id="returnFirst">
      MATCH (n: person)
      RETURN n
      LIMIT 1
  </select>
  ```
- json序列化后，结果为：
  ```json
    {
      "name": "张三",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    }
  ```


## 集合类型
因为运行时，方法在字节码中的集合泛型丢失，因此需要通过标签中的 `resultType` 属性告知 ngbatis，多行中单行结果的类型。
### 单栏位返回值

#### 基本类型
- PersonDao.java
  ```java
    List<String> returnNameTop10();
  ```
- PersonDao.xml
  ```xml
  <select id="returnNameTop10" resultType="java.lang.String">
      MATCH (n: person)
      RETURN n.person.name
      LIMIT 10
  </select>
  ```
- json序列化后，结果为：[ "张三", "李四", ... ]

#### POJO
- PersonDao.java
  ```java
      List<Person> returnTop10();
  ```
- PersonDao.xml
  ```xml
    <select id="returnTop10" resultType="your.domain.Person">
        MATCH (n: person)
        RETURN n
        LIMIT 10
    </select>
  ```
- json序列化后，结果为：
  ```json
  [
    {
      "name": "张三",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    },
    ...
  ]
  ```
  > 当返回值栏位只有一个时，读取内部属性映射到实体类属性

#### Map
- PersonDao.java
  ```java
    List<Map> returnTop10();
  ```
- PersonDao.xml
  ```xml
    <select id="returnTop10" resultType="java.util.Map">
        MATCH (n: person)
        RETURN n
        LIMIT 10
    </select>
  ```
- json序列化后，结果为：
  ```json
  [
    {
      "name": "张三",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    },
    ...
  ]
  ```
  > 当返回值栏位只有一个时，读取内部属性映射做为 map 的 key

### 多栏位返回值
#### POJO
- PersonDao.java
  ```java
      List<Person> returnPartTop10();
  ```
- PersonDao.xml
  ```xml
    <select id="returnPartTop10" resultType="your.domain.Person">
        MATCH (n: person)
        RETURN 
          n.person.name as name,
          n.person.age as age
        LIMIT 10
    </select>
  ```
- json序列化后，结果为：
  ```json
  [
    {
      "name": "张三",
      "age": 18,
      "birthday": null,
      "gender": null
    },
    ...
  ]
  ```


#### Map
- PersonDao.java
  ```java
    List<Map> returnPartTop10();
  ```
- PersonDao.xml
  ```xml
    <select id="returnPartTop10" resultType="java.util.Map">
        MATCH (n: person)
        RETURN 
          n.person.name as name,
          n.person.age as age
        LIMIT 10
    </select>
  ```
- json序列化后，结果为：
  ```json
  [
    {
      "name": "张三",
      "age": 18
    },
    ...
  ]
  ```
  > 当返回值栏位只有一个时，读取内部属性映射做为 map 的 key

## 复合对象类型（Path类型处理方式，三元组）
**注意**：下列例子是当前版本处理 Path 结果集的举例
### 声明复合对象类：
  - NRN2.java
    ```java
    package your.domain;

    public class NRN2 {
        private Person n;
        private Like r;
        private Person n2;
    }
    ```
### 非集合类型
  - PersonDao.java
    ```java
      NRN2 returnFirstRelation();
    ```
  - PersonDao.xml
    ```xml
    <select id="returnFirst">
        MATCH (n: person)-[r: like]->(n2: person)
        RETURN n, r, n2
        LIMIT 1
    </select>
    ```
### 集合类型
  - PersonDao.java
    ```java
      List<NRN2> returnRelationTop10();
    ```
  - PersonDao.xml
    ```xml
    <select id="returnRelationTop10" resultType="your.domain.NRN2">
        MATCH (n: person)-[r: like]->(n2: person)
        RETURN n, r, n2
        LIMIT 10
    </select>
    ```
  - > 使用 List<Map\> 做为返回值同样奏效，n, r, n2 为 map 的 key
