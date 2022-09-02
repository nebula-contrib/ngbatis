# 准备工作

## 大致介绍
Ngbatis 提供了两种方式为开发者提供便利
- 类似于 Mybatis-plus 的方式，提供一个基类让业务的`DAO`进行继承，不需要自己写 `nGQL` 就能完成单顶点、单边的增删改查。  
（详见[使用基类编写](./#?path=dev-example&file=dao-basic)）
- 类似于 Mybatis 的方式，支持自己编写复杂的 `nGQL` 或 `Cypher` 来完成复杂的业务查询与数据写入。（详见[自定义nGQL](./#?path=dev-example&file=custom-crud)）
  


下面，以 `Person` 与 `Like` 为例。

## Nebula Graph 中创建的 Schema （参考[CREATE TAG](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/10.tag-statements/1.create-tag/)、[CREATE EDGE](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/11.edge-type-statements/1.create-edge/)、[CREATE INDEX](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/14.native-index-statements/1.create-native-index/)）

```sql
CREATE tag `person` (
  `name` string NULL  , 
  `gender` string NULL  , 
  `age` int NULL  , 
  `birthday` date NULL  
);
```

```sql
CREATE edge `like` (`likeness` double NULL  );
```

```sql
-- 为查询创建索引
CREATE TAG INDEX `i_person_name_age` on `person`(`name`(50), `age`);
CREATE TAG INDEX `i_person_name` on `person`(`name`(50));
```

## 两种方式都需要的 `POJO` 类
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

到此，两种使用方式样例所用的 `POJO` 就已经创建完毕。接下来开始我们的介绍。