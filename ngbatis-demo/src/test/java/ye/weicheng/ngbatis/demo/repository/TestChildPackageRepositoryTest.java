package ye.weicheng.ngbatis.demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yeweicheng
 * @since 2022-06-18 5:13
 * <br>Now is history!
 */
@SpringBootTest
class TestChildPackageRepositoryTest {

    @Autowired
    private  TestChildPackageRepository repository;

    @Test
    void select1() {
        repository.select1();
    }
}