package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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

  @Test
  public void testFindLeafClass() {
    Set<Class<?>> classes = new HashSet<Class<?>>() {{
      add(A.class);
      add(E.class);
      add(F.class);
      add(G.class);
    }};
    Class<?> leafType = ReflectUtil.findNoForkLeafClass(classes, C.class);
    Assertions.assertEquals(D.class, leafType);

    leafType = ReflectUtil.findNoForkLeafClass(classes, E.class);
    Assertions.assertEquals(E.class, leafType);

    leafType = ReflectUtil.findNoForkLeafClass(classes, B.class);
    Assertions.assertEquals(B.class, leafType);
  }
}

/*
 * - A
 *   - B
 *     - C
 *       - D
 *         - F
 *         - G
 *     - E
 */
class A {}

class B extends A {}

class C extends B {}

class D extends C {}

class F extends D {}

class G extends D {}

class E extends B {}
