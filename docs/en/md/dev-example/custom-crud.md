# By Custom nGQL

- > When using this method, the familiarity with nGQL and cypher will be higher. Developers who are not familiar with it can learn about it through【[What Is nGQL](https://docs.nebula-graph.com.cn/3.1.0/3.ngql-guide/1.nGQL-overview/1.overview/)】.
- > In addition, the template engine used here is Beetl.
  > - [Beetl Docs-Delimiters And Placeholder Symbols](https://www.kancloud.cn/xiandafu/beetl3_guide/2138947), mainly look at placeholders. Ngbatis has made variable settings. If it is only parameter filling, you can ignore the use of delimiters. 【See [[Param Usage](./#?path=dev-example&file=parameter-use)] for details】
  However, parameter condition control and parameter loop delimiter are almost inevitable. Due to the difference in configuration of `Beetl` in `ngbatis`, if the delimiter is involved in the document, the `<% %>` will be replaced by `@ \n`, for example:
  >   ```diff
  >   - <%if ( aBool ) { 
  >   -                         
  >   - } %>                
  >   + @if ( aBool ) {
  >   +                       
  >   + @}                 
  >   ```
  > - [Beetl Docs-Functions Used to Process Parameters](https://www.kancloud.cn/xiandafu/beetl3_guide/2138956) 
  > - [Beetl Docs-Condition Control Statements](https://www.kancloud.cn/xiandafu/beetl3_guide/2138953)【See [Param Condition Control](./#?path=dev-example&file=parameter-if) for details】
  > - [Beetl Docs-Loop Statements](https://www.kancloud.cn/xiandafu/beetl3_guide/2138952)【See [Param Loop](./#?path=dev-example&file=parameter-for) for details】
  > - [Beetl-Online test widget](http://ibeetl.com/beetlonline/)


The same as [By Basic DAO](./#/?path=dev-example&file=dao-basic), We need to create a  XXXDao.java file and  XXXDao.xml file.

## Creating file in Dao layer
### Create a Dao corresponding to Person. If you do not need to use the basic class method, it is unnecessary to inherit NebulaDaoBasic.
```java
package your.domain;

import  org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;

public interface PersonDao {

}
```

### Create a file named PersonDao.xml file. The default location is: `/resources/mapper`
```xml
<mapper namespace="your.domain.PersonDao">

</mapper>
```
> XXXDao.java does not need to be annotated by `@Mapper` or `@Component`, it will be discovered by namespace and automatically registered as a bean.
> The premise is that the namespace needs to be in the scanBasePackages value under the @SpringBootApplication annotation.
> For example: @SpringBootApplication( scanBasePackages = { "your.domain", "org.nebula.contrib" } )

## How to let Java programs execute ` nGQL | cypher` through ngbatis

### Take a simple query statement as an example:
#### Adding interface in PersonDao.java
```java
  Integer select1();
```
> In the current version, the return value type and method parameter type of the interface. If it is a basic type, only the wrapper class is supported. For example, please write int as Integer.

#### Adding a tag in PersonDao.xml
```xml
  <select id="select1">
    RETURN 1
  </select>
```

> Integer result = personDao.select1(); // result : 1