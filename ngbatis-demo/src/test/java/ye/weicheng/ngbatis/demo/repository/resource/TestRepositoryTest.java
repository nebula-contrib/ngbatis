package ye.weicheng.ngbatis.demo.repository.resource;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.springframework.util.Assert.isTrue;

import java.util.Objects;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**
 * @author yeweicheng
 * @since 2023-08-04 17:48
 * <br>Now is history!
 */
@SpringBootTest
class TestRepositoryTest {

  @Autowired private TestRepository repository;
  @Resource(name = "NamedTestRepository")
  private ye.weicheng.ngbatis.demo.repository.resource.TestRepository otherTestRepository;
  
  @Test
  public void testSameClassName() {
    Integer num1 = otherTestRepository.testSameClassName();
    Integer num2 = repository.selectInt();
    isTrue(Objects.equals(num1, num2), "Results are 1 wrote by xml");
  }
  
}
