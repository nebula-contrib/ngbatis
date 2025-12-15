package ye.weicheng.ngbatis.demo.repository;

import java.util.List;
import java.util.Map;
import org.springframework.data.repository.query.Param;
import ye.weicheng.ngbatis.demo.pojo.Person;

/**
 * nGQL片段引用测试
 * 2023-9-7 12:25 lyw.
 */

public interface NgqlIncludeDao {
  Integer testInclude(@Param("myInt") Integer myInt);

  Integer returnAge(@Param("person")Person person);
  
  List<Map> selectByFilter(@Param("person") Person person);
}
