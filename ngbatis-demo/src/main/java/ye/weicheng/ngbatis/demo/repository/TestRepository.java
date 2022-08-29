// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.

package ye.weicheng.ngbatis.demo.repository;

import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.nebula.contrib.ngbatis.utils.Page;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.pojo.PersonLikePerson;

/**
 * 数据访问层 样例。
 *<p/>
 * @author yeweicheng
 * <br>Now is history!
 */
public interface TestRepository extends NebulaDaoBasic<Person, String> {

  Person selectPerson();

  Map selectPersonMap();

  List<Map> selectPersonsMap();

  List<Person> selectPersons();

  Set<Map> selectPersonsSet();

  List<String> selectListString();

  Integer selectInt();

  Person selectV();

  List<Person> selectListV();

  String selectString();

  String selectStringParam(String name);

  Integer selectIntParam(Integer age);

  Boolean selectBoolParam(Boolean finish);

  List<Person> selectCustomPage(Page<Person> page);

  List<Person> selectCustomPageAndName(Page<Person> page, String name);

  List<PersonLikePerson> selectPersonLikePerson();

  PersonLikePerson selectPersonLikePersonLimit1();

  ResultSet testMulti();

}
