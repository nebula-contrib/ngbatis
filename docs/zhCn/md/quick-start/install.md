# 安装与使用
## 在 `pom.xml` 中添加：
```xml
    <dependency>
        <groupId>org.nebula-contrib</groupId>
        <artifactId>ngbatis</artifactId>
        <version>1.1.0-beta</version>
    </dependency>
```

## 在 `application.yml` 配置数据源 
```yml
nebula:
  hosts: 127.0.0.1:19669, ip:port, ....
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

## 引入 ngbatis bean
### 项目中，只用到的 Nebula Graph 数据库
```java
@SpringBootApplication(
  exclude={ DataSourceAutoConfiguration.class }, 
  scanBasePackages = { "your.domain", "org.nebula.contrib" }  )
public class YourSpringbootApplication {

	public static void main(String[] args) {
		new SpringApplication(YourSpringbootApplication.class).run(args);
	}

}
```
### 项目中还有其他数据库
```java
@SpringBootApplication( scanBasePackages = { "your.domain", "org.nebula.contrib" } )
public class YourSpringbootApplication {

	public static void main(String[] args) {
		new SpringApplication(YourSpringbootApplication.class).run(args);
	}

}
```

### 主键生成器

#### 创建主键生成器
```java
import org.nebula.contrib.ngbatis.PkGenerator;

public class CustomPkGenerator implements PkGenerator {

    @Override
    public <T> T generate(String tagName, Class<T> pkType) {
        Object id = null; // 此处自行对 id 进行设值。
        return (T) id;
    }

}
```

#### 注册主键生成器
```java
@Configuration
public class PkGeneratorConfig {
    @Bean
    public PkGenerator pkGenerator() {
        return new CustomPkGenerator();
    }
}
```
