package org.nebula.contrib.ngbatis.proxy;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import org.objectweb.asm.Opcodes;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MethodModel;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Map;

import static org.nebula.contrib.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.NEED_SEALING_TYPES;

/**
 * 基于 ASM 对接口进行动态代理，并生成 Bean 代理的实现类
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
public class MapperProxyClassGenerator implements Opcodes {

    /**
     * 获取DAO接口对应的动态代理类名称
     *
     * @param cm 已经扫描的类模型
     * @return
     */
    private String getFullNameType(ClassModel cm) {
        return getFullNameType((cm.getNamespace().getName() + PROXY_SUFFIX));
    }

    /**
     * 通过类的全限定名，获取类在JVM中的类名
     *
     * @param className 接口全限定名
     * @return
     */
    private String getFullNameType(String className) {
        return className.replace(".", "/");
    }

    /**
     * 根据 ClassModel 对象中扫描得到的信息，<br>
     * 生成代理类，并将字节码设置到 ClassModel 对象中<br>
     *
     * @param cm DAO 类模型
     * @return DAO 接口对应的字节码
     */
    public byte[] setClassCode(ClassModel cm) {
        String fullNameType = getFullNameType(cm);

        ClassWriter cw = new ClassWriter(0);
        // public class XXX extends Object implement XXX
        cw.visit(V1_8, ACC_PUBLIC, fullNameType, null, "java/lang/Object",
                new String[] { getFullNameType(cm.getNamespace().getName()) });
        // 无参构造
        constructor(cw);
        // 生成代理方法
        methods(cw, cm);
        // 完成
        cw.visitEnd();
        byte[] code = cw.toByteArray();
        cm.setClassByte(code);

        writeFile(cm);

        return code;
    }

    /**
     * 根据扫描得到的类模型获取方法，并让类访问器进行访问
     *
     * @param cw asm 的类访问器
     * @param cm DAO 接口类模型
     */
    private void methods(ClassWriter cw, ClassModel cm) {
        // 读取配置，并根据配置向 class 文件写人代理方法
        Map<String, MethodModel> methods = cm.getMethods();
        for (Map.Entry<String, MethodModel> entry : methods.entrySet()) {
            method(cw, cm, entry);
        }
    }

    /**
     * 生成代理方法
     *
     * @param cw      asm 类访问器
     * @param cm      扫描所得类模型
     * @param mmEntry 方法名与方法模型映射
     */
    private void method(ClassWriter cw, ClassModel cm,
            Map.Entry<String, MethodModel> mmEntry) {
        String methodName = mmEntry.getKey();
        MethodModel mm = mmEntry.getValue();
        /*
         * return Mapper.invoke( "接口名 namespace", "方法名 method", new Object[]{
         * arg1, arg2, ... } ); ----- start
         */
        Method method = mm.getMethod();
        String methodSignature = ReflectUtil.getMethodSignature(mm);
        MethodVisitor mapper = cw.visitMethod(ACC_PUBLIC, methodName,
                methodSignature, null, null);

        mapper.visitCode();
        String className = cm.getNamespace().getName();
        mapper.visitLdcInsn(className);
        mapper.visitLdcInsn(mm.getId());
        int parameterCount = addParams(mapper, mm.getParameterCount());
        mapper.visitMethodInsn(INVOKESTATIC,
                getFullNameType(MapperProxy.class.getName()), "invoke",
                "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        /*
         * -------------------------------- end --------------------------------
         */

        // *2，每多一个方法参数，需要多定义 2 个局部变量，下标变量
        // +3： 3 个固定参数位，namespace、methodName、args
        mapper.visitMaxs(Integer.MAX_VALUE, Integer.MAX_VALUE);

        // 检查类型转换
        Class<?> returnType = mm.getReturnType();
        mapper.visitTypeInsn(CHECKCAST,
                getFullNameType(returnType.getTypeName()));

        // 基本类型封箱
        // sealingReturnType(mapper, returnType ); // FIXME 处理基本类型的封箱

        int returnTypeInsn = getReturnTypeInsn(returnType);
        mapper.visitInsn(returnTypeInsn);
        mapper.visitEnd();
    }

    private void sealingReturnType(MethodVisitor mapper, Class<?> returnType) {
        Class<?> basicReturnType = ReflectUtil.sealingBasicType(returnType);
        if (NEED_SEALING_TYPES.contains(returnType)) {
            String typeName = getFullNameType(basicReturnType.getName());
            String methodName = returnType.getName() + "Value";
            mapper.visitMethodInsn(INVOKEVIRTUAL, typeName, methodName, "()I",
                    false);
        }
    }

    /**
     *
     * int IRETURN = 172; // visitInsn int LRETURN = 173; // - int FRETURN =
     * 174; // - int DRETURN = 175; // - int ARETURN = 176; // - int RETURN =
     * 177; // -
     * 
     * @param returnType
     * @return
     */
    private int getReturnTypeInsn(Class returnType) {
        return returnType == long.class ? LRETURN
                : returnType == int.class ? IRETURN
                        : returnType == double.class ? DRETURN
                                : returnType == float.class ? FRETURN
                                        : returnType == void.class ? RETURN
                                                : ARETURN;
    }

    /**
     * Object[] args3 = new Object[] { arg1, arg2, arg3... }
     *
     * @param mv
     * @param parameterCount
     */
    private int addParams(MethodVisitor mv, int parameterCount) {
        // 获取被代理方法的参数个数，当前变量的栈中位置后推一位
        int varLocation = parameterCount + 1;
        // Object[] argN = new Object[ parameterCount ] --------- start
        mv.visitLdcInsn(parameterCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitVarInsn(ASTORE, varLocation); // 将数组引用存到局部变量栈1号的位置
        mv.visitVarInsn(ALOAD, varLocation);
        // ------------------------------------------------------------ end
        // argN[ i ] = argI
        for (int i = 0; i < parameterCount; i++) {
            // 读取变量 argN
            mv.visitVarInsn(ALOAD, varLocation);
            // 访问欲赋值的下标
            mv.visitLdcInsn(i);
            // 访问值变量
            mv.visitVarInsn(ALOAD, i + 1); // 方法传入的参数，起始位是 1，第 0 位为 this
            // 将值变量设置到对应下标中
            mv.visitInsn(AASTORE);
        }
        return parameterCount;
    }

    /**
     * 空参构造函数生成方法 public XXX() {
     *
     * }
     */
    private void constructor(ClassWriter cw) {
        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V",
                null, null);
        // 将this参数入栈
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
                "()V", false);
        constructor.visitInsn(RETURN);
        // 指定局部变量栈的空间大小
        constructor.visitMaxs(1, 1);
        // 构造方法的结束
        constructor.visitEnd();
    }

    /**
     * 将生成的字节码，写入本地文件，形成 .class 文件供调试时使用
     * 
     * @param cm
     */
    private void writeFile(ClassModel cm) {
        try {
            File file = new File(
                    "asm-debug\\" + getFullNameType(cm) + ".class");
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(cm.getClassByte());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
