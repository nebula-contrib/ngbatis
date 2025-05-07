package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import ye.weicheng.ngbatis.demo.pojo.ColumnAlias;

/**
 * 列别名测试用例-DAO
 * @author yeweicheng
 * @since 2022-09-10 9:35
 * <br>Now is history!
 */
public interface ColumnAliasDao extends NebulaDaoBasic<ColumnAlias, String> {

  ColumnAlias propsToObj();

}
