// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.io;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ye.weicheng.ngbatis.annotations.TimeLog;
import ye.weicheng.ngbatis.config.ParseCfgProps;
import ye.weicheng.ngbatis.exception.ParseException;
import ye.weicheng.ngbatis.exception.ResourceLoadException;
import ye.weicheng.ngbatis.models.ClassModel;
import ye.weicheng.ngbatis.models.MethodModel;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ye.weicheng.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static ye.weicheng.ngbatis.utils.ReflectUtil.NEED_SEALING_TYPES;
import static ye.weicheng.ngbatis.utils.ReflectUtil.getNameUniqueMethod;


/**
 * xml 文件加载器，主要作用有：
 * <ul>
 *     <li>读取并解析 mapper/*.xml 文件</li>
 *     <li>对 xml 声明的类与方法进行存储，供生成动态代理使用 </li>
 * </ul>
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class MapperResourceLoader extends PathMatchingResourcePatternResolver {

    private static Logger log = LoggerFactory.getLogger( MapperResourceLoader.class );

    private MapperResourceLoader() {
        super();
    }

    public MapperResourceLoader( ParseCfgProps parseConfig ) {
        this.parseConfig = parseConfig;
    }

    protected ParseCfgProps parseConfig;

    @TimeLog( name = "xml-load", explain = "mappers xml load completed : {} ms")
    public Map<String, ClassModel> load() {
        Map<String, ClassModel> resultClassModel = new HashMap<>();
        try {
            Resource[] resources = getResources(parseConfig.getMapperLocations());
            for( Resource resource : resources ) {
                resultClassModel.putAll( parseClassModel(resource) );
            }
        } catch (IOException e) {
            throw new ResourceLoadException( e );
        }
        return resultClassModel;
    }

    public Map<String,ClassModel> parseClassModel( Resource resource ) throws IOException {
        Map<String, ClassModel> result = new HashMap<>();
        // 从资源中获取文件信息，IO 读取
        Document doc = Jsoup.parse(resource.getInputStream(), "UTF-8", "http://example.com/");
        // 传入 xml 解析器，获取 xml 信息
        Elements elementsByTag = doc.getElementsByTag(parseConfig.getMapper());
        for( Element element : elementsByTag ) {
            ClassModel cm = new ClassModel();
//            cm.setResourceLoader( this );
            cm.setResource( resource );
            // 获取 namespace
            match( cm, element, "namespace", parseConfig.getNamespace() );
            // 获取 子节点
            List<Node> nodes = element.childNodes();
            // 便历子节点，获取 MethodModel
            Map<String, MethodModel>  methods = parseMethodModel( cm.getNamespace(), nodes );
            cm.setMethods( methods );
            result.put( cm.getNamespace().getName() + PROXY_SUFFIX, cm );
        }
        return result;
    }

    private Map<String, MethodModel> parseMethodModel( Class namespace, List<Node> nodes ) {
        Map<String, MethodModel> methods = new HashMap<>();
        for( Node methodNode : nodes ) {
            if( methodNode instanceof Element ) {
                MethodModel methodModel = parseMethodModel(methodNode);
                Method method = getNameUniqueMethod(namespace, methodModel.getId());
                methodModel.setMethod(method);
                Assert.notNull( method, "接口 " + namespace.getName() +" 中，未声明 xml 中的出现的方法：" + methodModel.getId() );
                checkReturnType(method, namespace);
                methods.put( methodModel.getId(), methodModel );
            }
        }
        return methods;
    }

    private void checkReturnType(Method method, Class namespace) {
        Class<?> returnType = method.getReturnType();
        if (NEED_SEALING_TYPES.contains( returnType )) {
            throw new ResourceLoadException("目前不支持返回基本类型，请使用对应的包装类，接口：" + namespace.getName() + "." + method.getName() );
        }
    }


    protected MethodModel parseMethodModel( Node node ) {
        MethodModel model = new MethodModel();
        match( model, node, "id", parseConfig.getId() );
        match( model, node, "parameterType", parseConfig.getParameterType() );
        match( model, node, "resultType", parseConfig.getResultType() );

        List<Node> nodes = node.childNodes();
        model.setText( nodesToString( nodes ) );
        return model;
    }

    protected String nodesToString( List<? extends Node> nodes ) {
        StringBuilder builder = new StringBuilder();
        for( Node node : nodes ) {
            if( node instanceof TextNode ) {
                builder.append( ((TextNode) node).getWholeText() );
                builder.append( "\n");
            }
        }
        String mapperText = builder.toString();
        String unescape = Entities.unescape(mapperText);
        return unescape;
    }

    private void match( Object model, Node node, String javaAttr, String attr ) {
        String attrTemp = null;
        try {
            String attrText = node.attr(attr);
            if(Strings.isBlank( attrText ) ) return;
            attrTemp = attrText;
            Field field = model.getClass().getDeclaredField(javaAttr);
            Class<?> type = field.getType();
            field.setAccessible( true );
            if( type == Class.class ) {
                field.set( model, Class.forName( attrText ) );
            } else {
                field.set( model, attrText );
            }
            field.setAccessible( false );
        } catch (ClassNotFoundException e) {
            throw new ParseException( "类型 " + attrTemp + " 未找到" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
