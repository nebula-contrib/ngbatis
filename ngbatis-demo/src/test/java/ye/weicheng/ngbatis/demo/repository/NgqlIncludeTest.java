package ye.weicheng.ngbatis.demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;

/**
 * TODO
 * 2023-9-7 15:23 lyw.
 */
@SpringBootTest
public class NgqlIncludeTest {

  @Autowired
  private NgqlIncludeDao ngqlIncludeDao;
  @Autowired
  private NgqlInclude4diffMapperDao ngqlInclude4diffMapperDao;

  @Test
  public void test() {
    System.out.println("nGQL引用测试:" + ngqlIncludeDao.testInclude(1));
    Person person = new Person();
    person.setAge(18);
    System.out.println("nGQL引用额外参数测试:" + ngqlIncludeDao.returnAge(person));
    System.out.println("nGQL跨mapper引用测试:" + ngqlInclude4diffMapperDao.testInclude(1));

  }

}
