package ye.weicheng.ngbatis.demo.service.impl;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ye.weicheng.ngbatis.demo.repository.TestRepository;
import ye.weicheng.ngbatis.demo.service.PersonService;

/**
 * <p>Person业务类实例</p>
 * @author yeweicheng
 * @since 2022-06-17 7:18
 * <br>Now is history!
 */
@Service
public class PersonServiceImpl implements PersonService {

  @Autowired
  private TestRepository repository;
}
