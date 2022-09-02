# Param Usage

Through【[By Custom nGQL](./#/?path=dev-example&file=custom-crud)】, we have known how to make the `nGQL | cypher` written by ourselves call and execute it into Nebula through Java.
Next, in this section, we will introduce how to pass parameters to the statements.
> **Attention**: The basic types of all parameters currently only support wrapper classes. For example, `int` needs to be written as `Integer`.

## Read Parameters in `nGQL | cypher`

### Named Parameters
> The parameters are annotated by @Param. ( org.springframework.data.repository.query.Param )
#### Basic Types
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

#### POJO or Map
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
> Parameter reading supports `.` operator

### Anonymous Parameters
- If there is no @Param annotation in the parameters declared in the interface, the index is used to obtain, that is: $p0、$p1、$p2、$p3 ...
- When the length of the parameter list is 1 and the type is POJO or Map, the attribute can be read directly


#### Basic Types
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

#### POJO or Map
##### When there is only one parameter:
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

##### When there are two or more parameters:
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

### The parameter acquisition of the collection type is consistent with the basic types.
- When anonymous, use $p0，$p1，...;
- When naming, directly use the parameter name in the annotation;
- If it is a collection of basic types, it can be directly passed in without complex processing.
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

## Conclusion
This completes the general introduction of parameter acquisition. If you have requirements for condition control and traversal after obtaining parameters, please move to【[Param Condition Control](./#/?path=dev-example&file=parameter-if)】、【[Param Loop](./#/?path=dev-example&file=parameter-for)】  
