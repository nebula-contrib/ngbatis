package ye.weicheng.ngbatis.demo.controller;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**
 * <p>Person类型的webapi示例</p>
 * @author yeweicheng
 * @since 2022-08-24 3:08
 * <br>Now is history!
 */
@Controller
@RequestMapping("/person")
public class PersonController {

  @Autowired
  private TestRepository dao;

  /**
   * <p>webapi: person/insert</p>
   * @param person http接口参数
   * @return
   */
  @PostMapping("insert")
  @ResponseBody
  public HashMap<String, Object> insert(@RequestBody Person person) {
    dao.insert(person);
    return new HashMap<String, Object>() {{
        put("code", 200);
      }};
  }

}
