package org.nebula.contrib.ngbatis.models;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * XXXDao.java 中的单个方法信息，以及 xml 中 子标签下的文本数据酷操作脚本 CQL
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
public class MethodModel {

    // ---------------- info in xml start ---------------------
    /**
     * 方法名
     */
    private String id;

    /**
     * nGQL 模板
     */
    private String text; // cql

    /**
     * 在方法中指定使用的 space
     */
    private String space;

    /**
     * xml 中配置的参数类型
     */
    private Class parameterType;

    /**
     * xml 中子标签所声明的 集合泛型
     */
    private Class resultType;

    /**
     * 反射所得的方法返回值
     */
    private Class returnType;

    /**
     * XXXDao 的方法对象
     */
    private Method method;

    /**
     * {@link #method} 参数列表的参数类型数组
     */
    private Class<?>[] parameterTypes;

    // ---------------- info in interface start ---------------------
    /**
     * 用于 asm 的方法签名。
     */
    private String signature;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public Class getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class parameterType) {
        this.parameterType = parameterType;
    }

    public Class getResultType() {
        return resultType;
    }

    public void setResultType(Class resultType) {
        this.resultType = resultType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Class<?>[] getParameterTypes() {
        return method == null ? parameterTypes : method.getParameterTypes();
    }

    public int getParameterCount() {
        return method == null ? parameterTypes.length
                : method.getParameterCount();
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class getReturnType() {
        return method == null ? returnType : method.getReturnType();
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    public Annotation[][] getParameterAnnotations() {
        if (method != null)
            return method.getParameterAnnotations();
        return new Annotation[getParameterCount()][];
    }
}
