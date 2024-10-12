package org.nebula.contrib.kg.controller;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.nebula.contrib.kg.pojo.Triplet;
import org.nebula.contrib.kg.service.DataService;
import org.nebula.contrib.kg.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yeweicheng
 * @since 2024-09-02 15:46
 * <br>Now is history!
 */
@RestController
@RequestMapping("/kg/data")
public class DataController {
  
  @Autowired
  private DataService service;

  @RequestMapping("next")
  public R<List<Triplet<String>>> next(String id) {
    return R.ok(service.next(id));
  }

}
