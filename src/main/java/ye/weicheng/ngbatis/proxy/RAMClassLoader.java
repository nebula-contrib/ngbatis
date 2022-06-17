// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import ye.weicheng.ngbatis.Env;
import ye.weicheng.ngbatis.models.ClassModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *将 {@link MapperProxyClassGenerator} 所代理生成的字节码，加载到内存的类加载器
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class RAMClassLoader extends ClassLoader {

    private Logger log = LoggerFactory.getLogger( RAMClassLoader.class );
    //记录需要让当前类加载器加载的类
    private Map<String, ClassModel> classModelMap;

    /**
     * @param classModelMap 某个路径下
     */
    public RAMClassLoader(Map<String, ClassModel> classModelMap) {
        super(Env.classLoader);
        this.classModelMap = classModelMap;
        for( Map.Entry<String, ClassModel> classModelEntry : classModelMap.entrySet() ) {
            loadClassCode( classModelEntry );
        }
    }

    private final Object lock = new Object();

    private void loadClassCode(Map.Entry<String, ClassModel> entry) {
        String className = entry.getKey();
        byte[] classByte = entry.getValue().getClassByte();
        log.info( "Proxy class had been load (代理类被加载): {}", className );
        defineClass( className , classByte, 0, classByte.length);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        synchronized ( lock ) {
            if (c == null) {
                // 不需要我们加载
                if (!classModelMap.containsKey(name)) {
                    c = Env.classLoader.loadClass(name);
                    log.info( "Class had been loaded: {}", name );
                } else {
                    throw new ClassNotFoundException("找不到该class");
                }
            }
            return c;
        }
    }

}
