/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package org.nebula.contrib.ngbatis.binding;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {

  @Test
  public void serverTest() {
    NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
    nebulaPoolConfig.setMaxConnSize(1);
    NebulaPool pool = new NebulaPool();
    try {
      Assert
          .assertTrue(
              pool.init(Arrays.asList(new HostAddress("127.0.0.1", 9669)), nebulaPoolConfig));
      Session session = pool.getSession("root", "nebula", true);

      ResultSet resultSet = session.execute("SHOW SPACES");
      Assert.assertTrue(resultSet.isSucceeded());
      System.out.println("access NebulaGraph success!");
    } catch (Exception e) {
      e.printStackTrace();
      assert false;
    }
  }

}
