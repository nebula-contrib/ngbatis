package ye.weicheng.ngbatis.demo.repository;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static ye.weicheng.ngbatis.proxy.MapperProxy.ENV;

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


    @Test
    public void testExecuteWithParameter() throws IOErrorException {
        Session session1 = ENV.openSession();
        ResultSet resultSet = session1.executeWithParameter("USE test;" +
                        "INSERT VERTEX `person` (\n" +
                        "                `name`  \n" +
                        "        )\n" +
                        "        VALUES 'name' : (\n" +
                        "                $name \n" +
                        "        );",
                new HashMap<String, Object>() {{
                    put("name", "");
                }}
        );
        System.out.println( resultSet.getErrorMessage() );
        System.out.println( resultSet );
        assert resultSet.isSucceeded();
    }
}