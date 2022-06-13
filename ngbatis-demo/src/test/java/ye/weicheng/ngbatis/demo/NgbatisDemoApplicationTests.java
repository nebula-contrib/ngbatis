// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;
import ye.weicheng.ngbatis.Env;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class NgbatisDemoApplicationTests {

	@Autowired
	private TestRepository repository;

	static {
		Env.classLoader = Person.class.getClassLoader();
	}

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



}
