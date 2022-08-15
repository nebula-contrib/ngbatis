package org.nebula.contrib.ngbatis.io;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.nebula.contrib.ngbatis.annotations.TimeLog;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.exception.ParseException;
import org.nebula.contrib.ngbatis.exception.ResourceLoadException;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.nebula.contrib.ngbatis.utils.Page;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.nebula.contrib.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.NEED_SEALING_TYPES;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getNameUniqueMethod;


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

    /**
     * 加载多个开发者自建的 XXXDao.xml 资源。
     * @return 所有 XXXDao 的全限定名 与 当前接口所对应 XXXDao.xml 解析后的全部信息
     */
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

    /**
     * 解析 单个开发者自定义的 XXXDao.xml 文件
     * @param resource 单个 XXXDao.xml 的资源文件
     * @return 单个 XXXDao 的全限定名 与 当前接口所对应 XXXDao.xml 解析后的全部信息
     * @throws IOException
     */
    public Map<String,ClassModel> parseClassModel( Resource resource ) throws IOException {
        Map<String, ClassModel> result = new HashMap<>();
        // 从资源中获取文件信息，IO 读取
        Document doc = Jsoup.parse(resource.getInputStream(), "UTF-8", "http://example.com/");
        // 传入 xml 解析器，获取 xml 信息
        Elements elementsByTag = doc.getElementsByTag(parseConfig.getMapper());
        for( Element element : elementsByTag ) {
            ClassModel cm = new ClassModel();
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

    /**
     * 解析 一个 XXXDao 的多个方法。
     * @param namespace XXXDao 类
     * @param nodes XXXDao.xml 中 &lt;mapper&gt; 下的子标签。即方法标签。
     * @return 返回当前XXXDao类的所有方法信息Map，k: 方法名，v：方法模型（即 xml 里一个方法标签的全部信息）
     */
    private Map<String, MethodModel> parseMethodModel( Class namespace, List<Node> nodes ) {
        Map<String, MethodModel> methods = new HashMap<>();
        List<String> methodNames = getMethodNames( nodes );
        for( Node methodNode : nodes ) {
            if( methodNode instanceof Element ) {
                MethodModel methodModel = parseMethodModel(methodNode);
                Method method = getNameUniqueMethod(namespace, methodModel.getId());
                methodModel.setMethod(method);
                Assert.notNull( method, "接口 " + namespace.getName() +" 中，未声明 xml 中的出现的方法：" + methodModel.getId() );
                checkReturnType(method, namespace);
                pageSupport( method, methodModel, methodNames, methods );
                methods.put( methodModel.getId(), methodModel );
            }
        }
        return methods;
    }

    /**
     * 一次性对当前接口中，需要分页的方法进行支持。
     * @param method 接口方法
     * @param methodModel 方法模型（即 xml 里一个方法标签的全部信息）
     * @param methodNames 当前接口的所有方法名（用于判断自动生成的接口是否已经有同名，如果已有则不再重复创建）
     * @param methods 用于将需要分页的接口，自动追加两个接口，用于生成动态代理
     */
    private void pageSupport(Method method, MethodModel methodModel, List<String> methodNames, Map<String, MethodModel> methods) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Class<?>> parameterTypeList = Arrays.asList(parameterTypes);
        if( parameterTypeList.contains(Page.class) ) {
            int pageParamIndex = parameterTypeList.indexOf(Page.class);
            MethodModel pageMethod = createPageMethod(methodModel, methodNames, parameterTypes, pageParamIndex);
            methods.put( pageMethod.getId(), pageMethod );

            MethodModel countMethod = createCountMethod( methodModel, methodNames, parameterTypes);
            methods.put( countMethod.getId(), countMethod );
        }
    }

    /**
     * 创建分页中的 条数统计接口 的方法模型
     * @param methodModel 分页原始方法模型
     * @param methodNames 当前接口的所有方法名（用于判断自动生成的接口是否已经有同名，如果已有则不再重复创建）
     * @param parameterTypes 方法的全部参数类型
     * @return
     */
    private MethodModel createCountMethod(MethodModel methodModel, List<String> methodNames, Class<?>[] parameterTypes) {
        String methodName = methodModel.getId();
        String countMethodName = String.format("%s$Count", methodName);
        Assert.isTrue( !methodNames.contains( countMethodName ), "There is a method name conflicts with " + countMethodName );
        MethodModel countMethodModel = new MethodModel();
        countMethodModel.setParameterTypes( parameterTypes );
        countMethodModel.setId(countMethodName);
        String cql = methodModel.getText();

        String with = cql.replaceAll("(RETURN)|(return)", "WITH");

        cql = String.format( "%s\t\tRETURN count(*);", with );

        countMethodModel.setText(cql);
        countMethodModel.setReturnType( Long.class );
        return countMethodModel;
    }

    /**
     * 创建 分页中查询范围条目方法 的模型
     * @param methodModel 分页原始方法模型
     * @param methodNames 当前接口的所有方法名（用于判断自动生成的接口是否已经有同名，如果已有则不再重复创建）
     * @param parameterTypes 方法的全部参数类型
     * @param pageParamIndex 分页参数处在参数列表中的下标位
     * @return 查询范围条目方法 的方法模型
     */
    private MethodModel createPageMethod(MethodModel methodModel, List<String> methodNames, Class<?>[] parameterTypes, int pageParamIndex) {
        String methodName = methodModel.getId();
        String pageMethodName = String.format("%s$Page", methodName);
        Assert.isTrue( !methodNames.contains( pageMethodName ), "There is a method name conflicts with " + pageMethodName );
        MethodModel pageMethodModel = new MethodModel();
        pageMethodModel.setParameterTypes( parameterTypes );
        pageMethodModel.setId(pageMethodName);
        String cql = methodModel.getText();
        if( parameterTypes.length > 1) {
            String format = "%s\t\tSKIP $p%d.startRow LIMIT $p%d.pageSize";
            cql = String.format(format, cql, pageParamIndex, pageParamIndex );
        } else {
            String format = "%s\t\tSKIP $startRow LIMIT $pageSize";
            cql = String.format( format, cql );
        }
        pageMethodModel.setText(cql);
        pageMethodModel.setResultType( methodModel.getResultType() );
        pageMethodModel.setReturnType( methodModel.getMethod().getReturnType() );
        return pageMethodModel;
    }

    /**
     * 从xml标签中，获取所有的方法名。
     * @param nodes xml 中 &lt;mapper&gt; 下的子标签
     * @return 当前 &lt;mapper&gt; 所声明的所有子标签 id
     */
    private List<String> getMethodNames(List<Node> nodes) {
        return nodes.stream().map(node -> {
            if (node instanceof Element) {
                return ((Element) node).id();
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * 对暂未支持的 未封箱基础类型 进行检查并给出友好报错
     * @param method 查询方法
     * @param namespace 接口类
     */
    private void checkReturnType(Method method, Class namespace) {
        Class<?> returnType = method.getReturnType();
        if (NEED_SEALING_TYPES.contains( returnType )) {
            throw new ResourceLoadException("目前不支持返回基本类型，请使用对应的包装类，接口：" + namespace.getName() + "." + method.getName() );
        }
    }

    /**
     * 解析 &lt;mapper&gt; 下的一个子标签，形成方法模型
     * @param node &lt;mapper&gt;  子标签
     * @return 方法模型
     */
    protected MethodModel parseMethodModel( Node node ) {
        MethodModel model = new MethodModel();
        match( model, node, "id", parseConfig.getId() );
        match( model, node, "parameterType", parseConfig.getParameterType() );
        match( model, node, "resultType", parseConfig.getResultType() );

        List<Node> nodes = node.childNodes();
        model.setText( nodesToString( nodes ) );
        return model;
    }

    /**
     * 获取&lt;mapper&gt; 子标签默认插槽内的文本
     * @param nodes
     * @return
     */
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

    /**
     * 将 xml 中的标签属性及文本，与模型进行匹配并设值。（模型包含 类模型与方法模型）
     * @param model
     * @param node
     * @param javaAttr
     * @param attr
     */
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
