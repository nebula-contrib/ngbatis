package ye.weicheng.ngbatis.demo.controller;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**.
 * @author yeweicheng.
 * @since 2022-08-24 3:08 <br>.
 *     Now is history.
.*/
@Controller
@RequestMapping("/person")
public class PersonController {

  @Autowired private TestRepository dao;

  @PostMapping("insert")
  @ResponseBody
  public HashMap<String, Object> insert(@RequestBody Person person) {
    dao.insert(person);
    return new HashMap<String, Object>() {
      {
        put("code", 200);
      }
    };
  }
}
