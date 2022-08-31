# Param Condition Control

> Syntax reference:【[Beetl-Condition Control](https://www.kancloud.cn/xiandafu/beetl3_guide/2138953)】
> Due to the difference in configuration of `Beetl` in `ngbatis`, the `<% %>` will be replaced by `@ \n`, for example：
  ```diff
  - <%if ( aBool ) { 
  -                         
  - } %>                
  + @if ( aBool ) {
  +                       
  + @}                 
  ```

## Use of Ternary Expressions
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

## Use of if
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
## Use of if-else
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

## Use of switch-case
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
- > **Attention**: the switch variable placed here cannot be null

## Use of select case
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


## Use of decode
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
## Conclusion
At this time, the methods that can be used for condition control are basically introduced. Judgment can only be made when inputting parameters. The return value cannot be controlled by conditions.