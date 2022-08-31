# 条件控制

> 语法参考【[Beetl 条件控制](https://www.kancloud.cn/xiandafu/beetl3_guide/2138953)】
> 因配置的差异，文档中如涉及界定符，则由文档中的 <% %> 替换成 @，如：
  ```diff
  - <%if ( aBool ) { 
  -                         
  - } %>                
  + @if ( aBool ) {
  +                       
  + @}                 
  ```

## 三元表达式的使用
- PersonDao.java
    ```java
        Person selectByNameIfNotEmpty( String name );
    ```

- PersonDao.xml
    ```xml
        <select id="selectByNameIfNotEmpty">
            MATCH (n: person)
            WHERE 1 == 1 
            ${ isEmpty( p0 ) ? '' : 'AND n.person.name == $p0' }
            RETURN n
            LIMIT 1
        </select>
    ```

## if 的使用
- PersonDao.java
    ```java
        // params = { age: 18 }
        Person selectByNameAndAge( Person person, Map<String, Object> params );
    ```
- PersonDao.xml
    ```xml
      <select id="selectByNameAndAge">
          MATCH (n: person)
          WHERE 1 == 1
          @if ( isNotEmpty( $p0.name ) ) {
            AND n.person.name == $p0.name
          @}
          @if ( isNotEmpty( $p1.age ) ) {
            AND n.person.age > $p1.age
          @}
          RETURN n
          LIMIT 1
      </select>
    ```
## if-else 的使用
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectMarriageable( @Param("gender") String gender );
    ```

- PersonDao.xml
    ```xml
        <select id="selectMarriageable">
            MATCH (n: person)
            WHERE 1 == 1 
            @if ( gender == 'F' ) {
              AND n.person.age >= 20
            @} else {
              AND n.person.age >= 22
            @}
            RETURN n
            LIMIT 1
        </select>
    ```

## switch case的使用
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectMarriageable( @Param("gender") String gender );
    ```

- PersonDao.xml
    ```xml
        <select id="selectMarriageable">
            MATCH (n: person)
            WHERE 1 == 1 
            @switch( gender ){
            @   case 'F':
                    AND n.person.age >= 20  
            @     break;
            @   case 'M':
                    AND n.person.age >= 22
            @     break;
            @   default:
                    AND n.person.age >= 24
            @}
            RETURN n
            LIMIT 1
        </select>
    ```
- > **注意**: 此处放入 switch 的变量，不可为 null

## select case 的使用
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectByGender( @Param("gender") String gender );
    ```

- PersonDao.xml
    ```xml
        <select id="selectMarriageable">
            MATCH (n: person)
            WHERE 1 == 1 
            @select( gender ){
            @   case 'F', 'M':
                    AND n.person.gender is not null
            @   default:
                    AND n.person.gender is null
            @}
            RETURN n
            LIMIT 1
        </select>
    ```


## decode 函数的使用
- PersonDao.java
    ```java
        // org.springframework.data.repository.query.Param
        Person selectMarriageable( @Param("gender") String gender );
    ```

- PersonDao.xml
    ```xml
        <select id="selectMarriageable">
            MATCH (n: person)
            WHERE 1 == 1 
            ${ decode( gender, 
              "F", "AND n.person.age >= 20", 
              "M", "AND n.person.age >= 22", 
              "AND n.person.age >= 24" 
            ) }
            RETURN n
            LIMIT 1
        </select>
    ```
## 总结
到此，可用于条件判断的方式基本介绍完成。只能用于入参时的判断。返回值不能使用条件控制。