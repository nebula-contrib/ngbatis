package ye.weicheng.ngbatis.demo.config;

import org.springframework.stereotype.Component;
import ye.weicheng.ngbatis.PkGenerator;

/**
 * 主键生成样例
 *
 * @author yeweicheng
 * @since 2022-06-14 12:32
 * <br>Now is history!
 */
@Component
public class PkGeneratorDemo implements PkGenerator {

    @Override
    public <T> T generate(String tagName, Class<T> pkType) {
        Long id = System.currentTimeMillis();
        if(pkType == String.class) {
            return (T) String.valueOf( id );
        }
        return (T) id;
    }

}
