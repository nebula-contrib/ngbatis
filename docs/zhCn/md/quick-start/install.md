# 安装与使用
## 在 `pom.xml` 中添加：
```xml
    <dependency>
        <groupId>org.nebula-contrib</groupId>
        <artifactId>ngbatis</artifactId>
        <version>1.1.2</version>
    </dependency>
```

### SNAPSHOT 版本
```xml
    <dependency>
        <groupId>org.nebula-contrib</groupId>
        <artifactId>ngbatis</artifactId>
        <version>1.2.0-SNAPSHOT</version>
    </dependency>
```
```xml
	<repositories>
		<repository>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</snapshots>
			<id>ossrh</id>
			<name>Nexus Snapshot Repository</name>
			<url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
		</repository>
	</repositories>
```

## 在 `application.yml` 配置数据源 
```yml
nebula:
  ngbatis:
    # ^v1.1.2
    # 连接使用 nebula-java 中的 SessionPool 
    # 虽然是 1.1.2 的特性，
    # 但该版本仅能使用 xml 中或者实体类注解的`@Space` 中对应的 space。
    # 仅声明在 yml 中，并且开启此选项时，会出现 SessionPool null 的问题，
    # 可升级至 1.2.0-SNAPSHOT 版本。
    use-session-pool: true 
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
  scanBasePackages = { "org.nebula.contrib", "your.domain" }  )
public class YourSpringbootApplication {

	public static void main(String[] args) {
		new SpringApplication(YourSpringbootApplication.class).run(args);
	}

}
```
### 项目中还有其他数据库
```java
@SpringBootApplication( scanBasePackages = { "org.nebula.contrib", "your.domain" } )
public class YourSpringbootApplication {

	public static void main(String[] args) {
		new SpringApplication(YourSpringbootApplication.class).run(args);
	}

}
```

## 主键生成器

#### 创建并注册主键生成器
```java
import org.nebula.contrib.ngbatis.PkGenerator;

@Component
public class CustomPkGenerator implements PkGenerator {

    @Override
    public <T> T generate(String tagName, Class<T> pkType) {
        Object id = null; // 此处自行对 id 进行设值。
        return (T) id;
    }

}
```
<!-- 
#### 注册主键生成器
```java
@Configuration
public class PkGeneratorConfig {
    @Bean
    public PkGenerator pkGenerator() {
        return new CustomPkGenerator();
    }
}
``` -->
