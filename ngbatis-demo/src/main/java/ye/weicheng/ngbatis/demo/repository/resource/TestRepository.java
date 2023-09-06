package ye.weicheng.ngbatis.demo.repository.resource;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.annotation.Resource;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import ye.weicheng.ngbatis.demo.pojo.Person;

/**
 * 数据访问层 样例。
 *<p/>
 * @author yeweicheng
 * @since 2023-08-04 17:36
 * <br>Now is history!
 */
@Resource(name = "NamedTestRepository")
public interface TestRepository extends NebulaDaoBasic<Person, String> {
  
  Integer testSameClassName();

  Integer includeTest();
}
