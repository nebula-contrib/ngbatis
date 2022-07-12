# 框架执行过程详述
## 初始化
1. 交由Springboot启动扫描，入口声明配置为：[spring.factories](./blob/master/src/main/resources/META-INF/spring.factories) ，
2. 启动类为：[NgbatisContextInitialize](./blob/master/src/main/java/ye/weicheng/ngbatis/NgbatisContextInitializer.java)
3. 初始化过程
    1. 从springboot配置文件中读取NebulaPoolConfig连接信息
    2. 创建全局上下文：[MapperContext](./blob/master/src/main/java/ye/weicheng/ngbatis/proxy/MapperContext.java)
    3. 读取 NebulaDaoBasic 的模板文件并解析，存入上下文
        1. 读取由 cql.parser.mapper-tpl-location 指定的文件。执行方：[DaoResourceLoader](./blob/master/src/main/java/ye/weicheng/ngbatis/io/DaoResourceLoader.java)
        2. 由 jsoup 完成 xml 解析（方法名、nGQL模板）。执行方：[MapperResourceLoader](./blob/master/src/main/java/ye/weicheng/ngbatis/io/MapperResourceLoader.java)
    4. 读取用户创建的 XXXDao.xml 并解析，存入上下文
        1. 读取由 cql.parser.mapper-locations 指定的所有 xml 文件
        2. 使用 jsoup 逐个文件解析
            1. 解析xml文件，获取对应接口，并存成一个类模型：[ClassModel](./blob/master/src/main/java/ye/weicheng/ngbatis/models/ClassModel.java)
            2. 解析子标签，获取具体方法，并将子标签所包含的信息与其反射所得方法的信息存成一个方法模型：[MethodModel](./blob/master/src/main/java/ye/weicheng/ngbatis/models/MethodModel.java)
    5. 创建 NebulaPool 存入上下文
    6. 建立显式实体类型与数据库类型的关系，MapperContext.tagTypeMapping（默认为NebulaDaoBasic的泛型T的类型都会加载，如未使用 NebulaDaoBasic，开发时，可自行追加，如：put: k: "person", v: Person.class）
    7. 注册 XXXDao 对象形成由 spring 管理的 bean
        1. 通过类模型信息，由 asm 生成动态代理类（以字节码的形式存在于内存中），执行方：[MapperProxyClassGenerator](./blob/master/src/main/java/ye/weicheng/ngbatis/proxy/MapperProxyClassGenerator.java)
            1. 类字节码
            2. 空参构造字节码
            3. 方法字节码
                > 实际调用的方法为 MapperProxy.invoke 方法，传入自身的类信息与方法信息与运行时接收的参数，从上下文信息中获取对应的 nGQL。详情见下【运行时】
        2. 将代理类加载到 jvm 中，执行方：[RAMClassLoader](./blob/master/src/main/java/ye/weicheng/ngbatis/proxy/RAMClassLoader.java)
        3. 从类模型中读取接口全限定名，并将其对应的、由第2步加载的类注册成 spring bean 交由spring bean容器管理
    8. 批量注册结果集处理器，机制为：通过将结果集处理器继承抽象类[AbstractResultHandler](./blob/master/src/main/java/ye/weicheng/ngbatis/handler/AbstractResultHandler.java)，在创建对象时，在抽象类构造函数中注册进上下文，用于ResultSet内部不同类型的结果，即 nGQL 不同类型的返回值。使用详见【运行时】

> 至此完成 ngbatis 动态代理的初始化过程

## 应用配置，主类 [Env](./blob/master/src/main/java/ye/weicheng/ngbatis/Env.java)
1. 声明nGQL参数解析器，默认方案使用 beetl 模板引擎进行解析。[BeetlTextRender](./blob/master/src/main/java/ye/weicheng/ngbatis/binding/BeetlTextRender.java)
    > 替换方式为：自行实现 [TextResolver](./blob/master/src/main/java/ye/weicheng/ngbatis/TextResolver.java)并以 @Primary 的方式交由spring管理
2. 指定主键生成器（vertex id 与 edge rank 值设置器），可使用时间戳主键生成来获取主键，但建议按自身应用的架构自行实现主键生成。
    ```java
    @Configuration
    public class PkGeneratorConfig {
        @Bean
        public PkGenerator pkGenerator() {
            return new TimestampPkGenerator();
        }
    }
    ```
3. 可通过继承[AbstractResultHandler](./blob/master/src/main/java/ye/weicheng/ngbatis/handler/AbstractResultHandler.java)进行更多类型结果的实体化


## 运行时
1. 业务方通过反转注入的方式，将动态代理类注入到业务类中。
2. 业务类调用实际方法，并传入参数
3. 动态代理执行 MapperProxy.invoke( 接口名, 方法名, 参数列表 )
    1. 读取接口参数序列化器，并对接口参数进行序列化，将参数转换成与 xml 中占位符相对应的键值对
    2. 对 xml 中的占位符进行替换
    3. 获取 Session 并执行 executeWithParameter
    4. 结果集处理，结果集处理器路由[ResultResolver](./blob/master/src/main/java/ye/weicheng/ngbatis/ResultResolver.java)，默认为 [DefaultResultResolver](./blob/master/src/main/java/ye/weicheng/ngbatis/binding/DefaultResultResolver.java)，由方法模型中接口声明的返回值与额外声明的泛型resultType共同决定采用何种[ResultHandler](./blob/master/src/main/java/ye/weicheng/ngbatis/ResultHandler.java)对结果进行处理。**在这个小步骤中，完成 ORM 过程。**

## 开发者使用思路（如无特殊需求，复杂拓展请见【应用配置】）
1. 指定主键生成器，参考【应用配置.2】
2. 创建 XXXDao.java 
3. 创建 XXXDao.xml 并指定 namespace 为 XXXDao.java
4. 在 XXXDao.xml 中编写 nGQL
5. 业务调用时直接注入 XXXDao，调用对应方法即可获取并执行对应 nGQL，无需处理结果集，便能拿到所需 实体对象




    
