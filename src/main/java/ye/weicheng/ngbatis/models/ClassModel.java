// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.models;

import ye.weicheng.ngbatis.ResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * 类模型信息  <br>
 * xml 中 mapper 标签所声明的信息（类），包含属性与子标签（方法）
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ClassModel {

    public static final String PROXY_SUFFIX = "$Proxy";

    private Class namespace;

    private Map<String, MethodModel> methods;

    public MethodModel getMethod( String methodName ) {
        return methods.get( methodName );
    };

    private ResourceLoader resourceLoader;

    private Resource resource;

    private byte[] classByte;

    private Class clazz;

    public Class getNamespace() {
        return namespace;
    }

    public void setNamespace(Class namespace) {
        this.namespace = namespace;
    }

    public Map<String, MethodModel> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodModel> methods) {
        this.methods = methods;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public byte[] getClassByte() {
        return classByte;
    }

    public void setClassByte(byte[] classByte) {
        this.classByte = classByte;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
