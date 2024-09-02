package org.nebula.contrib.kg.service;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.nebula.contrib.kg.pojo.Triplet;

/**
 * @author yeweicheng
 * @since 2024-09-02 16:08
 * <br>Now is history!
 */
public interface DataService {

  List<Triplet<String>> next(String id);

}
