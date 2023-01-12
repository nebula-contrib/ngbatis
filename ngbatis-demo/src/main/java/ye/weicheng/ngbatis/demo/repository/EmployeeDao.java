package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022- All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import ye.weicheng.ngbatis.demo.pojo.Employee;

/**
 * @author yeweicheng
 * @since 2023-01-12 13:16
 *   <br> Now is history!
 */
public interface EmployeeDao extends NebulaDaoBasic<Employee, String> {
}
