package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.proxy.MapperProxy.ENV;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yeweicheng
 * @since 2022-06-18 5:13
 * <br>Now is history!
 */
@SpringBootTest
class TestChildPackageRepositoryTest {

  @Autowired
  private TestChildPackageRepository repository;

  @Test
  void select1() {
    repository.select1();
  }


  // @Test
  public void testExecuteWithParameter() throws IOErrorException {
    Session session1 = ENV.openSession();
    ResultSet resultSet = session1.executeWithParameter("USE test;"
        + "INSERT VERTEX `person` (\n"
        + "        `name`  \n"
        + "    )\n"
        + "    VALUES 'name' : (\n"
        + "        $name \n"
        + "    );",
      new HashMap<String, Object>() {{
          put("name", "");
        }}
    );
    System.out.println(resultSet.getErrorMessage());
    System.out.println(resultSet);
    assert resultSet.isSucceeded();
  }
}
