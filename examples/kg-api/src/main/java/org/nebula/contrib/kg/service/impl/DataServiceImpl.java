package org.nebula.contrib.kg.service.impl;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.nebula.contrib.kg.dao.DataDao;
import org.nebula.contrib.kg.pojo.Triplet;
import org.nebula.contrib.kg.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yeweicheng
 * @since 2024-09-02 16:12
 * <br>Now is history!
 */
@Service
public class DataServiceImpl implements DataService {

  @Autowired
  private DataDao<String> dao;

  @Override
  public List<Triplet<String>> next(String id) {
    return dao.selectTriplets(id);
  }
}
