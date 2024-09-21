package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * 配置的工具类，用于完成配置到对象的转换
 * 
 * @author yeweicheng
 * @since 2024-07-03 13:44
 * <br>Now is history!
 */
public class ConfigUtil {

  public static <T> T getConfig(ConfigurableEnvironment environment, String prefix,
      Class<T> clazz) {
    PropertySource<?> configurationProperties = environment.getPropertySources()
      .get("configurationProperties");
    if (configurationProperties == null) {
      return null;
    }
    Object sources = configurationProperties.getSource();
    List<ConfigurationPropertySource> sourceList = new ArrayList<>();
    if (sources instanceof Iterable) {
      for (Object source : (Iterable<?>) sources) {
        if (source instanceof ConfigurationPropertySource) {
          sourceList.add((ConfigurationPropertySource) source);
        }
      }

      Binder binder = new Binder(sourceList);
      Bindable<T> bindable = Bindable.of(ResolvableType.forClass(clazz));
      
      BindResult<T> result = binder.bind(prefix, bindable);
      if (result.isBound()) {
        return result.get();
      }
    }
    return null;
  }
  
}
