# 参数循环

> 遍历的部分跟 mybatis 的差异比较大，使用了 Beetl 的遍历语法。具体可参考官方文档【[1.10 循环语句](https://www.kancloud.cn/xiandafu/beetl3_guide/2138952)】
> 因配置的差异，文档中如涉及界定符，则由文档中的 <% %> 替换成 @ \n，如：
  ```diff
  - <%for ( item in list ) { 
  -                         
  - } %>                
  + @for ( item in list ) {
  +                       
  + @}                 
  ```

## 对Map的遍历，可用于动态查询
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        // person: { "name": "张三", "gender": "F" }
        Person selectByPerson( @Param("p") Person person );
    ```

- PersonDao.xml
    ```xml
        <select id="selectByPerson">
            MATCH (n: person)
            WHERE 1 == 1 
            @for ( entry in p ) {
              @if ( isNotEmpty( entry.value ) ) {
                AND n.person.`${ entry.key }` == $p.${ entry.key }
              @}
            @}
            RETURN n
            LIMIT 1
        </select>
    ```

## 对 List 遍历，可用于批处理
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        // personList: [{"gender":"F","name":"张三"},{"gender":"M","name":"王五"},{"gender":"F","name":"赵六"}]
        void insertPersonList( @Param("personList") List<Person> personList );
    ```

- 参数为：
    ```json
      :param personList => [{"gender":"F","name":"张三"},{"gender":"M","name":"王五"},{"gender":"F","name":"赵六"}]
    ```

- PersonDao.xml
    ```xml
        <insert id="insertPersonList">
            @for ( p in personList ) {
              INSERT VERTEX `person` ( name, gender ) VALUES '${ p.name }' : ( '${ p.name }', '${ p.gender }' );
            @}
        </insert>
    ```

- 执行的语句为：
    ```sql
        INSERT VERTEX `person` ( name, gender ) VALUES '张三' : ( '张三', 'F' );
        INSERT VERTEX `person` ( name, gender ) VALUES '王五' : ( '王五', 'M' );
        INSERT VERTEX `person` ( name, gender ) VALUES '赵六' : ( '赵六', 'F' );
    ```
<!-- 
### nebula >= v3.2.0  起，多了下面的用法，在修改数据的时候可以传参数变量名给数据库
  - PersonDao.xml
      ```xml
        <insert id="insertPersonList">
            @for ( p in personList ) {
              INSERT VERTEX `person` ( name, gender )
                VALUES '${ p.name }' : ( '$personList[${pLP.dataIndex}].name', '$personList[${pLP.dataIndex}].gender' );
            @}
        </insert>
      ```
      > 此处，当前元素是 xxx 时，`LP` 做为后缀，可用于多种循环变量的获取
      > - xxxLP.index：当前索引，从1开始
      > - xxxLP.dataIndex：当前索引，从0开始
      > - xxxLP.size：集合的长度
      > - xxxLP.first：是否是第一个
      > - xxxLP.last：是否是最后一个
      > - xxxLP.even：索引是否是偶数
      > - xxxLP.odd：索引是否是奇数

  - 执行的语句为：
      ```sql
        INSERT VERTEX `person` ( name, gender )
          VALUES '张三' : ( '$personList[0].name', '$personList[0].gender' );
        INSERT VERTEX `person` ( name, gender )
          VALUES '王五' : ( '$personList[1].name', '$personList[1].gender' );
        INSERT VERTEX `person` ( name, gender )
          VALUES '赵六' : ( '$personList[2].name', '$personList[2].gender' );
      ```
-->
