package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.time.Duration;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import ye.weicheng.ngbatis.demo.pojo.TimeTest;

/**
 * @author yeweicheng
 * @since 2023-06-07 17:17
 * <br>Now is history!
 */
public interface TimeTestDao extends NebulaDaoBasic<TimeTest, String> {
  Duration selectTenDaysTwoSec();
}
