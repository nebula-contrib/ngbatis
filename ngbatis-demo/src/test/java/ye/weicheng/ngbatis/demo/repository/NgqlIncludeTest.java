package ye.weicheng.ngbatis.demo.repository;

import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nebula.contrib.ngbatis.models.data.NgTriplet;
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
  
  @Test
  public void testFilter() {
    Person person = new Person();
    List<Map> ngTriplet = ngqlIncludeDao.selectByFilter(person);
    System.out.println(JSON.toJSONString(ngTriplet));
    person.setName("赵小洋");
    ngTriplet = ngqlIncludeDao.selectByFilter(person);
    System.out.println(JSON.toJSONString(ngTriplet));
  }

}
