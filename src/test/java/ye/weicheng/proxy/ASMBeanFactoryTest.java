package ye.weicheng.proxy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ye.weicheng.ngbatis.Env;
import ye.weicheng.ngbatis.models.ClassModel;
import ye.weicheng.ngbatis.models.MethodModel;
import ye.weicheng.ngbatis.proxy.ASMBeanFactory;
import ye.weicheng.repository.NativeRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import static ye.weicheng.ngbatis.models.ClassModel.PROXY_SUFFIX;
import static ye.weicheng.ngbatis.utils.ReflectUtil.getNameUniqueMethod;

public class ASMBeanFactoryTest {

    private ClassModel cm;

    @BeforeAll
    public void init() {
        ClassModel classModel = new ClassModel();
        classModel.setNamespace(NativeRepository.class);
        classModel.setMethods( new HashMap<String, MethodModel>() {{
            MethodModel methodModel = new MethodModel();
            methodModel.setText( "return 1");
            methodModel.setId( "test2" );
            methodModel.setMethod( getNameUniqueMethod( NativeRepository.class,"test2"));
            put( "test2", methodModel );

            MethodModel selectById = new MethodModel();
            selectById.setText( "return 1");
            selectById.setId( "selectById" );
            selectById.setMethod( getNameUniqueMethod( NativeRepository.class,"selectById"));
            put("selectById", selectById);

            MethodModel selectByNameAndType = new MethodModel();
            selectByNameAndType.setText( "return 1");
            selectByNameAndType.setId( "selectByNameAndType" );
            selectByNameAndType.setMethod( getNameUniqueMethod( NativeRepository.class,"selectByNameAndType"));
            put("selectByNameAndType", selectByNameAndType);
        }});
        this.cm = classModel;
    }

    @Test
    public void newInstance() throws NoSuchFieldException, IllegalAccessException {
        ASMBeanFactory asmBeanFactory = new ASMBeanFactory();
        byte[] code = asmBeanFactory.setClassCode(cm);

        try {
            FileOutputStream fos = new FileOutputStream(new File("D:\\work\\dudu\\magic-orm\\src\\test\\java\\" + getFullNameType( cm ) + ".class"));
            fos.write(code);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadClass();

        checkClass();
    }

    private void loadClass() {
        Env.classLoader = getClass().getClassLoader();
//        Env env = new Env();
//        env.mapperContext();
    }

    private void checkClass() {
        try {
            Class<?> aClass = Class.forName("ye.weicheng.repository.NativeRepository$NeoProxy");

            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                System.out.println( declaredMethod.getName() );
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    private String getFullNameType( ClassModel cm ) {
        return (cm.getNamespace().getName() + PROXY_SUFFIX).replace( ".", "/");
    }
}