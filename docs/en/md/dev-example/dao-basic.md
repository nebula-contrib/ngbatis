# By Basic DAO

## Create a Dao corresponding to `Person` and extends NebulaDaoBasic
```java
package your.domain;

import  org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;

public interface PersonDao extends NebulaDaoBasic<Person, String> {

}
```

## Create a file named `PersonDao.xml`. The default location is `/resources/mapper`
```xml
<mapper namespace="your.domain.PersonDao">

</mapper>
```

## Examples in the service layer
```java

@Service
public class PersonServiceImpl {

  @Autowired private PersonDao dao;

  // Regardless of whether the attribute is empty or not, 
  // if there is a corresponding ID value in the database, 
  // other properties will be overwritten
  public void insert( Person person ) {
    dao.insert( person );
  }

  // Write only non empty properties
  public void insertSelective( Person preson ) {
    dao.insertSelective( person );
  }

  // Here, the name of the `Person`'s primary key column is string, 
  // so the input parameter is `String` type
  public Person selectById( String id ) {
    return dao.selectById( id );
  }

  // Query by non empty properties
  public List<Person> selectBySelective( Person person ) {
    return dao.selectBySelective( person );
  }

  // Delete the vertex by id.
  // FIXME: it's not logic delete in this version.
  public void deleteLogicById( String id ) {
    dao.deleteLogicById( id );
  }

  // Establish the relationship between two nodes, 
  // which need to exist in the database.
  public void insertEdge( Person tom, Like like, Person jerry ) {
    dao.insertEdge( tom, like, jerry );
  }

  // Establish the relationship between two nodes, 
  // which need to exist in the database.
  // This interface is used when multiple relationships can be created between two vertexes.
  // Whether multiple relationships can be created depends on whether the second parameter has field comments with @Id
  public void insertEdge( Person tom, LikeWithRank like, Person jerry ) {
    dao.insertEdge( tom, like, jerry );
  }

  // Page's fullname is {@link org.nebula.contrib.ngbatis.utils.Page}
  public List<Person> selectPage( Page<Person> page ) {
    return dao.selectPage( page );
  }

  // Make sure whether there is a certain relationship between the two vertexes
  public boolean existsEdge( String startId, Class edgeType, String endId ) {
    return dao.existsEdge( startId, edgeType, endId );
  }

  // Find all upstream vertexes in a certain relationship through a specific vertex
  public List<Person> listStartNodes( Class edgeType, String endId ) {
    return dao.listStartNodes( edgeType, endId );
  }

  // Find the unique upstream vertex of a specific relationship 
  // through a specific vertex
  public Person startNode( Class edgeType, String endId ) {
    return dao.startNode( edgeType, endId );
  }

}

```