// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo.repository;


import ye.weicheng.ngbatis.demo.pojo.Person;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据访问层 样例
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface TestRepository {

    Person selectPerson();

    Map selectPersonMap();

    List<Map> selectPersonsMap();

    List<Person> selectPersons();

    Set<Map> selectPersonsSet();

    List<String> selectListString();

    Integer selectInt();

    String selectString();

}
