package org.nebula.contrib.ngbatis;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.nebula.contrib.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static org.nebula.contrib.ngbatis.proxy.MapperProxy.ENV;
import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.entityTypeAndIdType;
import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.vertexName;

import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Resource;
import org.nebula.contrib.ngbatis.config.NebulaJdbcProperties;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.nebula.contrib.ngbatis.io.DaoResourceLoader;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.nebula.contrib.ngbatis.proxy.MapperProxyClassGenerator;
import org.nebula.contrib.ngbatis.proxy.RamClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Ngbatis 创建动态代理的主程
 *
 * @author yeweicheng
 * @since 2022-06-17 10:01
 * <br>Now is history!
 */
class NgbatisBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

  private Logger log = LoggerFactory.getLogger(NgbatisBeanFactoryPostProcessor.class);
  private NebulaJdbcProperties nebulaJdbcProperties;
  private ParseCfgProps parseCfgProps;
  private ConfigurableApplicationContext context;
  private MapperProxyClassGenerator beanFactory = new MapperProxyClassGenerator();

  public NgbatisBeanFactoryPostProcessor(NebulaJdbcProperties nebulaJdbcProperties,
      ParseCfgProps parseCfgProps,
      ConfigurableApplicationContext context) {
    this.nebulaJdbcProperties = nebulaJdbcProperties;
    this.parseCfgProps = parseCfgProps;
    this.context = context;
  }

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    setBeans(configurableListableBeanFactory);
    NebulaPool nebulaPool = nebulaPool();
    mapperContext(nebulaPool);
  }

  private void setBeans(ConfigurableListableBeanFactory beanFactory) {
    ObjectProvider<PasswordDecoder> passwordDecoders =
      beanFactory.getBeanProvider(PasswordDecoder.class);

    PasswordDecoder passwordDecoder = passwordDecoders.getIfAvailable();
    nebulaJdbcProperties.setPasswordDecoder(passwordDecoder);
  }

  public MapperContext mapperContext(NebulaPool nebulaPool) {
    DaoResourceLoader daoBasicResourceLoader = new DaoResourceLoader(parseCfgProps, this.context);
    MapperContext context = MapperContext.newInstance();
    context.setResourceRefresh(parseCfgProps.isResourceRefresh());
    context.setNgbatisConfig(nebulaJdbcProperties.getNgbatis());
    Map<String, ClassModel> interfaces = daoBasicResourceLoader.load();
    Map<String, String> daoBasicTpl = daoBasicResourceLoader.loadTpl();
    context.setDaoBasicTpl(daoBasicTpl);
    context.setNebulaPool(nebulaPool);
    context.setInterfaces(interfaces);
    context.setNebulaPoolConfig(nebulaJdbcProperties.getPoolConfig());
    figureTagTypeMapping(interfaces.values(), context.getTagTypeMapping());

    registerBean(context);
    return context;
  }

  /**
   * 自动从代码中获取 实体类与数据库标签 的映射关系
   *
   * @param classModels  类模型
   * @param tagTypeMapping 实体类与数据库标签 （容器）
   */
  private void figureTagTypeMapping(
      Collection<ClassModel> classModels,
      Map<String, Class<?>> tagTypeMapping) {

    for (ClassModel classModel : classModels) {
      Class<?>[] entityTypeAndIdType = entityTypeAndIdType(classModel.getNamespace());
      if (entityTypeAndIdType != null) {
        Class<?> entityType = entityTypeAndIdType[0];
        String vertexName = vertexName(entityType);
        tagTypeMapping.putIfAbsent(vertexName, entityType);
      }
    }

  }

  /**
   * 为所有的动态代理类 注册Bean到SpringBoot
   *
   * @param context Ngbatis上下文
   */
  private void registerBean(MapperContext context) {
    Map<String, ClassModel> interfaces = context.getInterfaces();
    for (ClassModel cm : interfaces.values()) {
      beanFactory.setClassCode(cm);
    }
    RamClassLoader ramClassLoader = new RamClassLoader(context.getInterfaces());
    for (ClassModel cm : interfaces.values()) {
      try {
        String className = cm.getNamespace().getName() + PROXY_SUFFIX;
        registerBean(cm, ramClassLoader.loadClass(className));
        log.info("Bean had been registed  (代理类注册成bean): {}", className);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * 为单个动态代理类 注册Bean到SpringBoot
   *
   * @param cm  类模型
   * @param proxy 动态代理类
   */
  private void registerBean(ClassModel cm, Class proxy) {
    BeanDefinitionBuilder beanDefinitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(proxy);
    BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
    registerBean(getBeanName(cm), beanDefinition);
  }

  /**
   * 为所代理的类指定 Bean 名
   *
   * @param className    类名
   * @param beanDefinition Spring 的bean注册器
   */
  private void registerBean(String className, BeanDefinition beanDefinition) {
    BeanDefinitionRegistry beanFactory =
        (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
    beanFactory.registerBeanDefinition(className, beanDefinition);
  }

  /**
   * 获取 Bean 的名字
   *
   * @param cm 类模型
   * @return 根据类模型生成的 bean名
   */
  private String getBeanName(ClassModel cm) {
    Class<?> namespace = cm.getNamespace();
    Component cpnAnno = namespace.getAnnotation(Component.class);
    Resource resAnno = namespace.getAnnotation(Resource.class);
    boolean namedByComponent = cpnAnno != null && isNotBlank(cpnAnno.value());
    boolean namedByResource = resAnno != null && isNotBlank(resAnno.name());
    
    return namedByResource ? resAnno.name()
      : namedByComponent ? (cpnAnno.value() + PROXY_SUFFIX)
        : namespace.getSimpleName() + PROXY_SUFFIX;
  }


  /**
   * 创建 Nebula 连接池
   *
   * @return Nebula 连接池
   */
  public NebulaPool nebulaPool() {
    NebulaPool pool = new NebulaPool();
    try {
      pool.init(
          nebulaJdbcProperties.getHostAddresses(),
          nebulaJdbcProperties.getPoolConfig()
      );
    } catch (UnknownHostException e) {
      throw new RuntimeException("Can not connect to Nebula Graph");
    }
    return pool;
  }

  /**
   * create and init Nebula SessionPool
   * please use IntervalCheckSessionDispatcher.setNebulaSessionPool() instead.
   */
  @Deprecated
  public void setNebulaSessionPool(MapperContext context) {
    ENV.getDispatcher().setNebulaSessionPool(context);
  }

  /**
   * session pool create and init
   * please use IntervalCheckSessionDispatcher.initSessionPool() instead.
   * @param spaceName nebula space name
   * @return inited SessionPool
   */
  @Deprecated
  public SessionPool initSessionPool(String spaceName) {
    return ENV.getDispatcher().initSessionPool(spaceName);
  }

  @Override
  public int getOrder() {
    return 100;
  }
}
