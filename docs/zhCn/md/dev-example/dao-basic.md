# 使用基类读写

## 创建一个Person对应的Dao，并继承 NebulaDaoBasic
```java
package your.domain;

import  org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;

public interface PersonDao extends NebulaDaoBasic<Person, String> {

}
```

## 创建一个名为 PersonDao.xml 的文件，默认位置为：`/resources/mapper`
```xml
<mapper namespace="your.domain.PersonDao">

</mapper>
```

## 在 Service 层中举例
```java

@Service
public class PersonServiceImpl {

  @Autowired private PersonDao dao;

  // 不管属性是否为空，如果数据库中已有对应 id 的值，则覆盖
  public void insert( Person person ) {
    dao.insert( person );
  }

  // 仅写入非空属性
  public void insertSelective( Person preson ) {
    dao.insertSelective( person );
  }

  // 此处，Person 的主键栏 name 为 String ，则入参为 String
  public Person selectById( String id ) {
    return dao.selectById( id );
  }

  // 按属性查询
  public List<Person> selectBySelective( Person person ) {
    return dao.selectBySelective( person );
  }

  // FIXME 当前版本，这个接口尚不是逻辑删除，待修改。
  public void deleteLogicById( String id ) {
    dao.deleteLogicById( id );
  }

  // 确立两个节点的关系，两个节点需要在数据库中存在。
  public void insertEdge( Person tom, Like like, Person jerry ) {
    dao.insertEdge( tom, like, jerry );
  }

  // 确立两个节点的关系，两个节点需要在数据库中存在。
  // 此接口，同样使用于，两个节点间可以创建多条关系的情况。
  // 是否可以创建多条关系，取决于 第2个参数是否有 @Id 的栏位
  public void insertEdge( Person tom, LikeWithRank like, Person jerry ) {
    dao.insertEdge( tom, like, jerry );
  }

  // Page 为  {@link org.nebula.contrib.ngbatis.utils.Page}
  public List<Person> selectPage( Page<Person> page ) {
    return dao.selectPage( page );
  }

  // 判断两个节点是否存在某种关系
  public boolean existsEdge( String startId, Class edgeType, String endId ) {
    return dao.existsEdge( startId, edgeType, endId );
  }

  // 查找一个节点某种关系中的所有上游节点
  public List<Person> listStartNodes( Class edgeType, String endId ) {
    return dao.listStartNodes( edgeType, endId );
  }

  // 查找一个节点中，某种关系的唯一一个上游节点
  public Person startNode( Class edgeType, String endId ) {
    return dao.startNode( edgeType, endId );
  }

}

```