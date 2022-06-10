package ye.weicheng.repository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NativeRepository {

    Map selectById(@Param( "id") String id);
    List<Map> selectByNameAndType(@Param( "name") String name, @Param("type") String type);

}
