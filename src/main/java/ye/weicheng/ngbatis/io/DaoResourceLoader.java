// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.springframework.core.io.Resource;
import ye.weicheng.ngbatis.config.ParseCfgProps;
import ye.weicheng.ngbatis.exception.ResourceLoadException;
import ye.weicheng.ngbatis.proxy.NebulaDaoBasic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link NebulaDaoBasic 常规图操作接口 }所需的资源文件加载器。
 * @author yeweicheng
 * @since 2022-06-21 2:19
 * <br>Now is history!
 */
public class DaoResourceLoader extends MapperResourceLoader {

    public DaoResourceLoader(ParseCfgProps parseConfig) {
        super(parseConfig);
    }

    /**
     * 加载基类接口所需 nGQL 模板
     * @return 基类接口方法名 与 nGQL 模板的 Map
     */
    public Map<String, String> loadTpl() {
        try {
            Resource resource = getResource( parseConfig.getMapperTplLocation() );
            return parse( resource );
        } catch (IOException e) {
            throw new ResourceLoadException( e );
        }
    }

    /**
     * 资源文件解析方法。用于获取 基类方法与nGQL模板
     * @param resource 资源文件
     * @return 基类接口方法名 与 nGQL 模板的 Map
     * @throws IOException
     */
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
