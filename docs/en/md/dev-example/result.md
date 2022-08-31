# Result Mapping （The translation work is in progress...）

After obtaining the results from nebula, it is often impossible to obtain the result type required by the business. At this time, the developer needs to inform ngbatis of the type required by the specific business.

## Original Type [`ResultSet`](https://github.com/vesoft-inc/nebula-java/blob/master/client/src/main/java/com/vesoft/nebula/client/graph/data/ResultSet.java) (com.vesoft.nebula.client.graph.data.ResultSet)
If the returned value of the interface is the resultset of nebula, ngbatis will not do any processing, and will directly return it to the invoker. Developers can process the result set in the invoker.
- PersonDao.java
  ```java
    ResultSet returnResultSet();
  ```
- PersonDao.xml
  ```xml
  <select id="returnResultSet">
      RETURN 'Value wrapped by ResultSet'
  </select>
  ```
- > If the result set processing required by development cannot be met in the following types, you can use to obtain the return value of the original `ResultSet` type and make flexible processing by yourself. See [Source code of ResultSet](https://github.com/vesoft-inc/nebula-java/blob/master/client/src/main/java/com/vesoft/nebula/client/graph/data/ResultSet.java) for more details.


## Non Collection
At this time, the result type has been notified to ngbatis through the return value type of the method, so the type mapping can be completed without additional configuration.
### Basic Types
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
- After JSON serialization, the result is:
  ```json
    {
      "name": "Tom",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    }
  ```


## Collection
Because the collection generics of the method in the bytecode are lost at runtime, ngbatis needs to be informed of the type of row results in multiple rows through the 'resultType' attribute in the tag.
### Single Column Return Value

#### Basic Types
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
- After JSON serialization, the result is: [ "Tom", "Jerry", ... ]

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
- After JSON serialization, the result is: 
  ```json
  [
    {
      "name": "Tom",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    },
    ...
  ]
  ```
  > When there is only one column as return value, read the schema internal attribute and map it to the entity class attribute.

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
- After JSON serialization, the result is: 
  ```json
  [
    {
      "name": "Tom",
      "age": 18,
      "birthday": "Fri Aug 12 2022 06:39:37 GMT+0800", // java.util.Date
      "gender": null
    },
    ...
  ]
  ```
  > When there is only one column as return value, read the internal attribute map of the schema as the key of the map

### Multi Column Return Value
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
- After JSON serialization, the result is:
  ```json
  [
    {
      "name": "Tom",
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
- After JSON serialization, the result is:
  ```json
  [
    {
      "name": "Tom",
      "age": 18
    },
    ...
  ]
  ```
  > When there is only one column as return value, read the internal attribute map of the schema as the key of the map

## Compound object type (Path type processing method, triples as also)

**Attention**: The following example is an example of processing the path result set in the current version

### Declaring a Composite Object Class:
  - NRN2.java
    ```java
    package your.domain;

    public class NRN2 {
        private Person n;
        private Like r;
        private Person n2;
    }
    ```
### Non-Collection
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
### Collection
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
  - > Using List<Map> as the return value also works，n, r, n2 are the keys of the map
