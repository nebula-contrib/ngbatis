package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yeweicheng
 * @since 2022-11-24 17:38
 * <br> Now is history!
 */
public class ReflectUtilTest {
    
  @Test
  public void testIsCurrentTypeOrParentType() {
    boolean currentTypeOrParentType = ReflectUtil
        .isCurrentTypeOrParentType(Timestamp.class, Date.class);
    Assertions.assertTrue(currentTypeOrParentType);
  }

}