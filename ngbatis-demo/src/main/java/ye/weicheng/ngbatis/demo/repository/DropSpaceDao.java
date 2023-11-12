package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2023 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;

/**
 * 方法中指定该语句不使用space，比如说 create space 等语句-DAO
 * @author yeweicheng
 * @since 2023-11-10 13:18
 * <br>Now is history!
 */
public interface DropSpaceDao {
  
  void createSpace(String name);
  
  void dropSpace(String name);
  
  List<String> showTags();
  
}
