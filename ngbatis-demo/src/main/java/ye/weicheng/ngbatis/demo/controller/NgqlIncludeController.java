package ye.weicheng.ngbatis.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ye.weicheng.ngbatis.demo.repository.NgqlInclude4diffMapperDao;
import ye.weicheng.ngbatis.demo.repository.NgqlIncludeDao;

/**
 * nGQL片段引用测试
 * 2023-9-7 12:40 lyw.
 */

@RestController
@RequestMapping("/include")
public class NgqlIncludeController {
  @Autowired
  private NgqlIncludeDao ngqlIncludeDao;
  @Autowired
   private NgqlInclude4diffMapperDao ngqlInclude4diffMapperDao;

  @RequestMapping("/test")
  public String test() {
    int a = ngqlIncludeDao.testInclude(1);
    int b = ngqlInclude4diffMapperDao.testInclude(2);
    return a + "," + b;
  }
}
