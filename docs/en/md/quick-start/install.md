# Import and Config

## Adding in `pom.xml`：
```xml
    <dependency>
        <groupId>org.nebula-contrib</groupId>
        <artifactId>ngbatis</artifactId>
        <version>1.1.1</version>
    </dependency>
```

## Adding  data source configuration in `application.yml`
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

## Import ngbatis bean
### Nebula only, in your project
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
### Multi database including Nebula
```java
@SpringBootApplication( scanBasePackages = { "your.domain", "org.nebula.contrib" } )
public class YourSpringbootApplication {

	public static void main(String[] args) {
		new SpringApplication(YourSpringbootApplication.class).run(args);
	}

}
```

### Primary key generator

#### Declare primary key generator
```java
import org.nebula.contrib.ngbatis.PkGenerator;

public class CustomPkGenerator implements PkGenerator {

    @Override
    public <T> T generate(String tagName, Class<T> pkType) {
        Object id = null; // Set id value by yourself.
        return (T) id;
    }

}
```

#### Register primary key generator as bean
```java
@Configuration
public class PkGeneratorConfig {
    @Bean
    public PkGenerator pkGenerator() {
        return new CustomPkGenerator();
    }
}
```
