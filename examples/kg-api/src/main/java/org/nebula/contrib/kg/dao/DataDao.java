package org.nebula.contrib.kg.dao;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.nebula.contrib.kg.pojo.Triplet;
import org.springframework.data.repository.query.Param;

/**
 * @author yeweicheng
 * @since 2024-09-02 15:54
 * <br>Now is history!
 */
public interface DataDao<T> {

  List<Triplet<T>> selectTriplets(@Param("id") T id);

}
