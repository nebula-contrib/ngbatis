package ye.weicheng.ngbatis.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ye.weicheng.ngbatis.config.ParseCfgProps;
import ye.weicheng.ngbatis.exception.ResourceLoadException;
import ye.weicheng.ngbatis.models.ClassModel;
import ye.weicheng.ngbatis.models.MethodModel;
import ye.weicheng.ngbatis.proxy.NebulaDaoBasic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yeweicheng
 * @since 2022-06-21 2:19
 * <br>Now is history!
 */
public class DaoResourceLoader extends MapperResourceLoader {

    public DaoResourceLoader(ParseCfgProps parseConfig) {
        super(parseConfig);
    }

    public Map<String, String> loadTpl() {
        try {
            Resource resource = getResource( parseConfig.getMapperTplLocation() );
            return parse( resource );
        } catch (IOException e) {
            throw new ResourceLoadException( e );
        }
    }

    private Map<String, String> parse(Resource resource) throws IOException {
        Document doc = Jsoup.parse(resource.getInputStream(), "UTF-8", "http://example.com/");
        Map<String, String> result = new HashMap<>();
        Method[] methods = NebulaDaoBasic.class.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            Element elementById = doc.getElementById(name);
            if( elementById != null ) {
                List<TextNode> textNodes = elementById.textNodes();
                String tpl = nodesToString(textNodes);
                result.put( name, tpl );
            }
        }
        return result;
    }
}
