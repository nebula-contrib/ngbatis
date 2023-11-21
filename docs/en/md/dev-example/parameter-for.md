# Param Loop

> There is a big difference between ngbatis and mybatis in the loop part, and the loop syntax of Beetl is used. Please refer to official documents for details.【[1.10 Loop statement](https://www.kancloud.cn/xiandafu/beetl3_guide/2138952)】
> Due to the difference in configuration of `Beetl` in `ngbatis`, the `<% %>` will be replaced by `@ \n`, for example：

  ```diff
  - <%for ( item in list ) { 
  -                         
  - } %>                
  + @for ( item in list ) {
  +                       
  + @}                 
  ```

## Looping in Map , which can be used for dynamic query

- PersonDao.java

    ```java
        // org.springframework.data.repository.query.Param
        // person: { "name": "Diana", "gender": "F" }
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

## Looping in List , which can be used for batch processing

- PersonDao.java

    ```java
        // org.springframework.data.repository.query.Param
        // personList: [{"gender":"F","name":"Diana"},{"gender":"M","name":"Tom"},{"gender":"F","name":"Jerry"}]
        void insertPersonList( @Param("personList") List<Person> personList );
    ```

- The parameter is :

    ```json
      :param personList => [{"gender":"F","name":"Diana"},{"gender":"M","name":"Tom"},{"gender":"F","name":"Jerry"}]
    ```

- PersonDao.xml

    ```xml
        <insert id="insertPersonList">
            @for ( p in personList ) {
              INSERT VERTEX `person` ( name, gender ) VALUES '${ p.name }' : ( '${ p.name }', '${ p.gender }' );
            @}
        </insert>
    ```

- The statements to be executed are:

    ```sql
        INSERT VERTEX `person` ( name, gender ) VALUES 'Diana' : ( 'Diana', 'F' );
        INSERT VERTEX `person` ( name, gender ) VALUES 'Tom' : ( 'Tom', 'M' );
        INSERT VERTEX `person` ( name, gender ) VALUES 'Jerry' : ( 'Jerry', 'F' );
    ```
<!-- 
### Since nebula v3.2.0, the following usage has been added. When modifying data, the parameter variable name can be passed to the database.
  - PersonDao.xml
      ```xml
        <insert id="insertPersonList">
            @for ( p in personList ) {
              INSERT VERTEX `person` ( name, gender )
                VALUES '${ p.name }' : ( '$personList[${pLP.dataIndex}].name', '$personList[${pLP.dataIndex}].gender' );
            @}
        </insert>
      ```
    > Here, when the current element is xxx,  `LP` is used as the suffix, which can be used to obtain multiple loop variables.
    > - xxxLP.index: current index, starting from 1
    > - xxxLP.dataIndex: current index: starting from 0
    > - xxxLP.size: length of the collect
    > - xxxLP.first: is it the first
    > - xxxLP.last: is it the last
    > - xxxLP.even: whether the index is even
    > - xxxLP.odd: whether the index is odd

  - The statements to be executed are: 
      ```sql
        INSERT VERTEX `person` ( name, gender )
          VALUES 'Diana' : ( '$personList[0].name', '$personList[0].gender' );
        INSERT VERTEX `person` ( name, gender )
          VALUES 'Tom' : ( '$personList[1].name', '$personList[1].gender' );
        INSERT VERTEX `person` ( name, gender )
          VALUES 'Jerry' : ( '$personList[2].name', '$personList[2].gender' );
      ```
-->
