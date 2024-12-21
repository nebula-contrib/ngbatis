package org.nebula.contrib.ngbatis.session;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.nebula.contrib.ngbatis.SessionDispatcher.addSpaceToSessionPool;
import static org.nebula.contrib.ngbatis.proxy.MapperProxy.ENV;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.typeArg;

import java.util.Map;
import javax.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.Space;
import org.nebula.contrib.ngbatis.base.GraphBase;
import org.nebula.contrib.ngbatis.exception.ResourceLoadException;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.context.ApplicationContext;

/**
 * 用于处理读取空间配置相关的工具类
 *
 * @author yeweicheng
 * @since 2024-12-20 23:26
 * <br>Now is history!
 */
public class SpaceRouter {

  /**
   * 从实体类获取 space，并解析占位符 从类获取有两种来源：
   * <ol>
   *   <li>通过 @Table 注解</li>
   *   <li>通过 @Space 注解</li>
   *   <li>默认值：${ nebula.space }</li>
   *   <li>优先级：@Table > @Space > 默认值</li>
   *   <li>支持来自配置文件的占位符解析</li>
   *   <li>推荐使用 @Table，@Space 属于自定义注解，可能会在后续版本移除</li>
   * </ol>
   */
  public static String spaceFromEntity(Class<?> entityType) {
    String defaultSpace = ENV == null ? null : ENV.getSpace();
    if (entityType == null) {
      return defaultSpace;
    }

    boolean hasTable = entityType.isAnnotationPresent(Table.class);
    if (hasTable) {
      Table tableAnnotation = entityType.getAnnotation(Table.class);
      String space = tableAnnotation.schema();
      System.out.println("table space: " + space);
      if (isNotBlank(space)) {
        return space;
      }
    }

    boolean hasSpace = entityType.isAnnotationPresent(Space.class);

    if (hasSpace) {
      Space spaceAnnotation = entityType.getAnnotation(Space.class);
      String space = spaceAnnotation.name();
      if (isNotBlank(space)) {
        System.out.println("space: " + space);
        return space;
      }
    }

    return defaultSpace;
  }

  public static String spaceFromEntity(Class<?> entityType, ApplicationContext context) {
    String space = spaceFromEntity(entityType);
    return tryResolvePlaceholder(space, context);
  }

  /**
   * 通过DAO的类模型获取对应的space。 支持占位符，并从配置信息中读取。 在 xml 资源解析阶段调用，读取 space， 若 space 存在占位符，则解析占位符 并将解析后的
   * space 设置到类模型中 此时，space 已经用上配置值，是有效的图空间。 并以此空间名初始化 session pool，如果开启了 session pool
   *
   * @param cm 类模型
   */
  public static void setClassSpace(ClassModel cm, ApplicationContext context) {
    if (cm != null) {
      String space = cm.getSpace();
      if (isBlank(space)) {
        Class<?> daoClass = cm.getNamespace();
        // 从 dao 类的泛型中获取实体类
        Class<?> entityType = typeArg(daoClass, NebulaDaoBasic.class, 0);
        // 通过实体类的注解获取space
        space = spaceFromEntity(entityType, context);
      }
      cm.setSpace(space);
      addSpaceToSessionPool(cm.getSpace());
    }
  }

  /**
   * 设置不同接口方法的空间 未使用参数传递空间时，从配置文件中读取
   *
   * @param methodModel        方法模型
   * @param applicationContext 应用上下文
   */
  public static void setMethodSpace(
    MethodModel methodModel, ApplicationContext applicationContext) {
    if (!methodModel.isSpaceFromParam()) { // 未使用参数传递空间
      String space = methodModel.getSpace();
      if (isNotBlank(space)) {
        // 如果不为空，则尝试解析占位符
        space = tryResolvePlaceholder(space, applicationContext);
        methodModel.setSpace(space);
        addSpaceToSessionPool(space);
      }
    }
  }

  /**
   * 支持space从参数中获取
   *
   * @param cm                当前接口的类模型
   * @param mm                当前接口方法的方法模型
   * @param paramsForTemplate 从模板参数中获取空间名
   * @return 目标space
   */
  public static String getSpace(
    ClassModel cm, MethodModel mm, Map<String, Object> paramsForTemplate
  ) {
    boolean spaceFromParam = mm.isSpaceFromParam();
    String space = mm.getSpace() != null ? mm.getSpace() : cm.getSpace();
    if ("null".equals(space)) {
      space = ENV.getSpace();;
    }
    if (spaceFromParam && space != null) {
      // 从参数中获取space
      String paramSpace = ENV.getTextResolver().resolve(space, paramsForTemplate);
      // 让参数同样支持 ${xx.xx} 占位符
      return tryResolvePlaceholder(paramSpace);
    }
    return isBlank(space) ? ENV.getSpace() : space;
  }

  /**
   * 获取当前space，支持 @Space 跟 @Table 注解 如果均未指定，
   * 则返回默认space 用于 GraphBaseExt 通过标签名、边类型名称获取space
   *
   * @return 当前space
   */
  public static String getSpace(Map<String, Object> m1) {
    Object edgeType = m1.get("edgeType");
    Object tag = m1.get("tag");
    String entityTypeName = edgeType != null ? edgeType.toString()
      : tag != null ? tag.toString()
        : null;

    String defaultSpace = ENV.getSpace();
    if (entityTypeName == null) {
      return defaultSpace;
    }

    // 获取表名与实体类的映射
    Map<String, Class<?>> entityTypeMapping = ENV.getMapperContext().getTagTypeMapping();
    // 获取实体类
    Class<?> entityType = entityTypeMapping.get(entityTypeName);
    String space = SpaceRouter.spaceFromConfig(entityType);
    if (isNotBlank(space)) {
      // 已经是解析后的space
      return space;
    }
    return ENV.getSpace();
  }

  /**
   * 通过实体类获取对应的space。 支持占位符，并从配置信息中读取。
   *
   * @param entityType 实体类
   * @return space
   */
  public static String spaceFromConfig(Class<?> entityType) {
    return spaceFromConfig(entityType, ENV.getContext());
  }

  /**
   * 调用时机：在环境启动时，尚获得 context 时调用
   *
   * @param entityType 实体类
   * @param context    Spring 上下文
   * @return space 空间名，已经解析占位符
   */
  public static String spaceFromConfig(Class<?> entityType, ApplicationContext context) {
    String space = null;
    ClassModel cm = ENV.getMapperContext().computeEntityClassModelMap().get(entityType);
    if (cm != null) {
      space = cm.getSpace();
    }
    if (isBlank(space)) {
      space = spaceFromEntity(entityType, context);
    }
    return space;
  }

  /**
   * 利用Spring Environment 解析注解的值，用于 @Space 的 name 属性解析
   *
   * @param value 需要解析的值，可能是带占位符的 ${xx.xx} ，也可以是固定的字符串
   * @return resolveResult 解析结果
   * @throws IllegalArgumentException 当配置了 ${xx.xx} 占位符，且spring配置文件中未指定该配置时抛出
   * @author <a href="https://github.com/charle004">Charle004</a>
   */
  public static String tryResolvePlaceholder(String value) {
    return tryResolvePlaceholder(value, ENV.getContext());
  }

  /**
   * 利用Spring Environment 解析注解的值，用于 @Space 的 name 属性解析
   *
   * @param configKey 需要解析的值，可能是带占位符的 ${xx.xx} ，也可以是固定的字符串
   * @return resolveResult 解析结果
   * @throws IllegalArgumentException 当配置了 ${xx.xx} 占位符，且spring配置文件中未指定该配置时抛出
   * @author <a href="https://github.com/charle004">Charle004</a>
   */
  public static String tryResolvePlaceholder(String configKey, ApplicationContext context) {
    if (isBlank(configKey)) {
      return null;
    }
    String resolveResult = configKey;
    if (null != context) {
      try {
        resolveResult = context.getEnvironment().resolveRequiredPlaceholders(configKey);
      } catch (IllegalArgumentException e) {
        throw new ResourceLoadException(
          "name ( " + configKey + " ) missing configurable value"
        );
      }
    }
    return resolveResult;
  }

}
