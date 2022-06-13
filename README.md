
# NGBATIS

## NGBATIS是什么？

**NGBATIS** 是一款针对 [Nebula Graph](https://github.com/vesoft-inc/nebula) + Springboot 的数据库 ORM 框架。借鉴于 [MyBatis](https://github.com/mybatis/mybatis-3) 的使用习惯进行开发。

## 项目要求
- Springboot
- Maven

## 如何使用（可在克隆代码后，参考 ngbatis-demo 项目）
### 克隆代码到本地
#### [gitee](https://gitee.com/CorvusY/ngbatis.git)
```shell
git clone https://gitee.com/CorvusY/ngbatis.git
```
#### [github](https://github.com/CorvusYe/ngbatis.git)
```shell
git clone https://github.com/CorvusYe/ngbatis.git
```

### 本地生成 jar 包到 maven 仓库
```shell
cd ngbatis
mvn -DskipTests=true  install
```

### 在项目引入
```xml
  <dependency>
    <groupId>ye.weicheng</groupId>
    <artifactId>ngbatis</artifactId>
    <version>1.1-SNAPSHOT</version>
  </dependency>
```

### 配置数据库
在 application.yml 中添加配置 **将数据源修改成可访问到的NebulaGraph**
```yml
nebula:
  hosts: 127.0.0.1:19669
  username: root
  password: nebula
  space: test
  pool-config:
    min-conns-size: 0
    max-conns-size: 10
    timeout: 0
    idle-time: 0
    interval-idle: -1
    wait-time: 0
    min-cluster-health-rate: 1.0
    enable-ssl: false
```

## 日常开发示例
### 声明数据访问接口
```java
package ye.weicheng.ngbatis.demo.repository;

import ye.weicheng.ngbatis.demo.pojo.Person;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TestRepository {
    Person selectPerson();

    List<String> selectListString();

    List<Map> selectPersonsMap();
}

```
### 编写数据访问语句
resource/mapper/TestRepository.xml
```xml
<mapper
    namespace=
    "ye.weicheng.ngbatis.demo.repository.TestRepository"
>

    <select id="selectPerson" resultType="ye.weicheng.ngbatis.demo.pojo.Person">
        match (v:person) return v.person.name as name, v.person.age as age limit 1
    </select>

    <select id="selectListString" resultType="java.lang.String">
        match (v:person) return v.person.name as name limit 100
    </select>

    <select id="selectPersonsMap" resultType="java.util.Map">
        match (v:person) return v.person.name as name, v.person.age  as age limit 100
    </select>

</mapper>
```

## TODOLIST
- 对更多返回值类型进行支持
- 提供主键生成策略选择
- 使用连接池的方式，让多次访问数据库使用的 Session 进行共享。提高访问性能
- 处理 Springboot jar 命令启动时的 ClassLoader 问题
