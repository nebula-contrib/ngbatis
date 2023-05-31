# 自定义nGQL

- > 使用此方式的时候，对 nGQL、cypher 的熟悉度要求会高一些。还不太熟悉的开发者，可以通过【[什么是nGQL](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/1.nGQL-overview/1.overview/)】进行了解。
- > 另外提一下：这边使用的模板引擎是Beetl
  > - [Beetl官方文档](https://www.kancloud.cn/xiandafu/beetl3_guide/2138947)，主要看占位符。ngbatis已经做好了变量设置，如果只是参数填充，可以忽略定界符的使用。   
  【例子详见[如何传入参数](./parameter-use)】  
  但，参数条件控制跟参数循环定界符几乎不可避免。因`ngbatis`关于`beetl`配置的差异，文档中如涉及界定符，则由文档中的 <% %> 替换成 @ \n，如：
  >   ```diff
  >   - <%if ( aBool ) { 
  >   -                         
  >   - } %>                
  >   + @if ( aBool ) {
  >   +                       
  >   + @}                 
  >   ```
  > - [Beetl文档-用于处理参数的函数](https://www.kancloud.cn/xiandafu/beetl3_guide/2138956) 
  > - [Beetl文档-条件控制](https://www.kancloud.cn/xiandafu/beetl3_guide/2138953)【例子详见[参数条件控制](./parameter-if)】
  > - [Beetl文档-循环语句](https://www.kancloud.cn/xiandafu/beetl3_guide/2138952)【例子详见[参数遍历](./parameter-for)】
  > - [Beetl在线测试小工具](http://ibeetl.com/beetlonline/)


与[使用基类读写](./dao-basic)相同，需要编写一个 XXXDao.java 文件与 XXXDao.xml 文件。

## 新建文件
### 创建一个Person对应的Dao，如果不需要用到基类方法，可以不继承 NebulaDaoBasic
```java
package your.domain;

import  org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;

public interface PersonDao {

}
```

### 创建一个名为 PersonDao.xml 的文件，默认位置为：`/resources/mapper`
```xml
<!-- 如果 space 与 yml 中声明的一致，可不写 -->
<mapper namespace="your.domain.PersonDao" space="test">

</mapper>
```
> XXXDao.java 无需经过 @Mapper 或者 @Component 进行注解，而是通过 namespace 进行发现，并自动注册成 Bean。
> 前提是：namespace 需要在 @SpringBootApplication 注解下的 scanBasePackages 值中。
> 如：@SpringBootApplication( scanBasePackages = { "your.domain", "org.nebula.contrib" } )

## 如何让 java 程序通过 ngbatis 执行 nGQL | cypher

### 以一个简单的查询语句为例：
#### 在 PersonDao.java 中追加接口
```java
  Integer select1();
```
> 目前版本中，接口的返回值类型与方法参数类型，如果是基本类型，仅支持包装类，如 int 请写成 Integer。

#### 在 PersonDao.xml 中新增一个标签
```xml
  <!-- 如果 space 与 yml 中声明的或 mapper 的 space 一致，可不写 -->
  <select id="select1" space="test">
    RETURN 1
  </select>
```

> Integer result = personDao.select1(); // result : 1