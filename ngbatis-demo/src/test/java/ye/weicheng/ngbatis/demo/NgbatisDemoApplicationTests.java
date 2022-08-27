// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.client.graph.data.ResultSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.NRN2;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;
import org.nebula.contrib.ngbatis.utils.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class NgbatisDemoApplicationTests {

	@Autowired
	private TestRepository repository;

	@Test
	void selectPerson()  {
		Object person = repository.selectPerson();
		System.out.println(JSON.toJSONString( person ));
	}

	@Test
	void selectPersonMap()  {
		Map person = repository.selectPersonMap();
		System.out.println(JSON.toJSONString( person ));
	}

	@Test
	void selectPersons()  {
		List<Person> persons = repository.selectPersons();
		System.out.println(JSON.toJSONString( persons ));
	}

	@Test
	void selectPersonsMap()  {
		List<Map> person = repository.selectPersonsMap();
		System.out.println(JSON.toJSONString( person ));
	}

	@Test
	void selectPersonsSet()  {
		Set<Map> person = repository.selectPersonsSet();
		System.out.println(JSON.toJSONString( person ));
	}

	@Test
	void selectListString() {
		List<String> firstPersonName = repository.selectListString();
		System.out.println( firstPersonName );
	}

	@Test
	void selectInt() {
		int i = repository.selectInt();
		System.out.println( i );
	}

	@Test
	void selectString() {
		String str = repository.selectString();
		System.out.println( str );
	}

	@Test
	void selectV() {
		Person str = repository.selectV();
		System.out.println( str );
	}


	@Test
	void selectListV() {
		List<Person> str = repository.selectListV();
		System.out.println( str );
	}


	@Test
	void selectStringParam() {
		String name = repository.selectStringParam( "经由数据库传输中文" );
		System.out.println( name );
	}

	@Test
	void selectIntParam() {
		Integer name = repository.selectIntParam( 12 );
		System.out.println( name );
	}

	@Test
	void selectBoolParam() {
		Boolean name = repository.selectBoolParam( false );
		System.out.println( name );
	}


	@Test
	void selectCustomPage() {
		Page<Person> page = new Page<>();
		page.setPageSize( 3 );
		page.setPageNo(1);
		List<Person> name = repository.selectCustomPage( page );
		System.out.println( JSON.toJSONString( name ));
		System.out.println( JSON.toJSONString( page ));
	}

	@Test
	void selectCustomPageAndName() {
		Page<Person> page = new Page<>();
		page.setPageSize( 3 );
		page.setPageNo(1);
		List<Person> name = repository.selectCustomPageAndName( page , "丁小碧" );
		System.out.println( JSON.toJSONString( name ));
		System.out.println( JSON.toJSONString( page ));
	}


	@Test
	public void selectNRN2() {
		List<NRN2> nrn2s = repository.selectNRN2();
		System.out.println( JSON.toJSONString( nrn2s ));
	}

	@Test
	public void selectNRN2Limit1() {
		NRN2 nrn2 = repository.selectNRN2Limit1();
		System.out.println( JSON.toJSONString( nrn2 ));
	}

	@Test
	public void testMulti() {
		ResultSet resultSet = repository.testMulti();
		System.out.println( resultSet );
	}

	@Test
	public void testStringPropNull_insert() {
		Person person = new Person();
		String genderNullTest = "genderNullTest";
		person.setName(genderNullTest);
		repository.insert( person );
		Person personDb = repository.selectById(genderNullTest);
		System.out.println( JSON.toJSONString( personDb ) );
		assert personDb.getGender() == null;
	}

}
