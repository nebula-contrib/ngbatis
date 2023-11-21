# Prepare Work

## Summary

Ngbatis provides two ways for developers to access nebula.

- Close to Mybatis-plus, providing a basic `DAO` to be extends, unnecessary to write any `nGQL` to operate single table, include vertex and edge. (See [By Basic DAO](./dao-basic) for more details)
- Close to Mybatis, supporting developers to write complex `nGQL` or `Cypher` to finish read or write data. (See [By Custom nGQL](./custom-crud) for more details)
  
Take  `Person` 与 `Like` as examples.

## Create Schema in Nebula (refer [CREATE TAG](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/10.tag-statements/1.create-tag/)、[CREATE EDGE](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/11.edge-type-statements/1.create-edge/)、[CREATE INDEX](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/14.native-index-statements/1.create-native-index/))

```sql
CREATE tag `person` (
  `name` string NULL  , 
  `gender` string NULL  , 
  `height` double NULL ,
  `age` int NULL  , 
  `birthday` date NULL  
);
```

```sql
CREATE edge `like` (`likeness` double NULL  );
```

```sql
-- create index for query
CREATE TAG INDEX `i_person_name_age` on `person`(`name`(50), `age`);
CREATE TAG INDEX `i_person_name` on `person`(`name`(50));
```

## Necessary `POJO` for two ways'

### Person.java

```java
package your.domain;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import lombok.Data;

@Data
@Table(name = "person")
public class Person {
    @Id
    private String name;
    private String gender;
    @ValueType(Double.class)
    private BigDecimal height;
    private Integer age;
    private Date birthday;
}
```

### Like.java

```java
package your.domain;

import lombok.Data;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "like")
public class Like {
    private Double likeness;
}
```

### LikeWithRank.java

```java
package your.domain;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "like")
public class LikeWithRank {
    @Id
    private Long rank;
    private Double likeness;
}
```

By now, the 'POJO' used in the two usage examples has been created. Let's start our coding journey.
