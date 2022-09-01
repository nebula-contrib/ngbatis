package ye.weicheng.ngbatis.demo.issues;

import com.alibaba.fastjson.JSON;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**
 * https://github.com/nebula-contrib/ngbatis/issues/35.
 * @author yeweicheng
 * @since 2022-09-01 20:20
 * <br>Now is history!
 */
@SpringBootTest
public class TestIssue35 {

    @Autowired
    private TestRepository repository;

    @Test
    public void selectBySelectiveWhenIdIsNull() {
        Person person = new Person();
        person.setAge(18);
        List<Person> people = repository.selectBySelective(person);
        System.out.println(JSON.toJSONString(people));
    }

    @Test
    public void selectBySelective() {
        Person person = new Person();
        person.setName("赵小洋");
        person.setAge(18);
        List<Person> people = repository.selectBySelective(person);
        System.out.println(JSON.toJSONString(people));
    }

}
