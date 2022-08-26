package org.nebula.contrib.ngbatis.proxy;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.util.Map;
import org.nebula.contrib.ngbatis.Env;
import org.nebula.contrib.ngbatis.NgbatisContextInitializer;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将 {@link MapperProxyClassGenerator} 所代理生成的字节码，加载到内存的类加载器
 *
 * @author yeweicheng <br>
 *     Now is history!
 */
public class RAMClassLoader extends ClassLoader {

  private Logger log = LoggerFactory.getLogger(RAMClassLoader.class);
  // 记录需要让当前类加载器加载的类
  private Map<String, ClassModel> classModelMap;

  /**
   * 对扫描xml所得的多个类模型信息进行处理。 将所有涉及的类事先加载到 jvm 中
   *
   * @param classModelMap 某个路径下
   */
  public RAMClassLoader(Map<String, ClassModel> classModelMap) {
    super(Env.classLoader);
    this.classModelMap = classModelMap;
    for (Map.Entry<String, ClassModel> classModelEntry :
        classModelMap.entrySet()) {
      loadClassCode(classModelEntry);
    }
  }

  private final Object lock = new Object();

  /**
   * 加载单个类模型，并将其涉及的类事先加载到 jvm 中
   *
   * @param entry 接口全限定名与类模型的键值对
   */
  private void loadClassCode(Map.Entry<String, ClassModel> entry) {
    String className = entry.getKey();
    byte[] classByte = entry.getValue().getClassByte();
    log.info("Proxy class had been load (代理类被加载): {}", className);
    defineClass(className, classByte, 0, classByte.length);
  }

  /**
   * 被 {@link NgbatisContextInitializer} 所调用，<br>
   * 在 spring bean容器注册bean前，把待注册的 bean 对应的类型加载到 jvm 中
   *
   * @param name 动态代理类的全限定名
   * @return 动态代理类
   * @throws ClassNotFoundException 当代理类或者其方法涉及的类型不存在于当前加载链中时抛出
   */
  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> c = findLoadedClass(name);
    synchronized (lock) {
      if (c == null) {
        // 不需要我们加载
        if (!classModelMap.containsKey(name)) {
          c = Env.classLoader.loadClass(name);
          log.info("Class had been loaded: {}", name);
        } else {
          throw new ClassNotFoundException("找不到该class");
        }
      }
      return c;
    }
  }
}
