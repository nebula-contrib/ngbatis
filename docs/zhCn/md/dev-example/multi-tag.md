# 基类实现多标签 ^v1.1.2

## 实现的效果
- insert时可以一次同时将 Person 和 Employee 的属性写入数据库
- select时可以一次同时将 Person 和 Employee 的属性读出，但如果该节点具备其他标签，则不会读出

## 实体类
```java
@Data
@Table(name = "employee")
public class Employee extends Person {
  private String position;
}
```

> DAO的实现方式与单标签的实现方式一致

## DAO
```java

import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import your.domain.pojo.Employee;

public interface EmployeeDao extends NebulaDaoBasic<Employee, String> {
}
```

## DAO XML
```xml
<mapper namespace="your.domain.repository.EmployeeDao">
    
</mapper>
```