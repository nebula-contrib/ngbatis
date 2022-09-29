# 框架特性

## 一、集成
- [x] 支持通过简单配置，快速完成 Nebula Graph 与 Springboot 的整合

## 二、单表（Vertex、Edge）操作，无需写  `nGQL | cypher`
> 用法参见【[使用基类编写](./#?path=dev-example&file=dao-basic)】  

API | 用法说明
--|--
insert(T entity) | 插入 Vertex，空值覆盖
insertSelective(T entity) | 插入 Vertext，空值跳过
selectById(ID id) | 通过主键获取节点
selectBySelective(T entity) | 按实体属性值查询
insertEdge(S startNode, R edge, E endNode) | 插入关系
selectPage(Page<T> page) | 分页查询
existsEdge(ID startId, Class edgeType, ID endId) | 判断两个节点是否有关系
listStartNodes(Class edgeType, ID endId) | 查找一个节点某种关系中的所有上游节点
startNode(Class edgeType, ID endId) | 查找一个节点中，某种关系的唯一一个上游节点

## 三、使用 xml 的方式，集中管理  `nGQL | cypher`
> 用法参见【[自定义nGQL](./#?path=dev-example&file=custom-crud)】  

扫描指定资源包，并获得 `nGQL | cypher` 模板，在模板的基础上做操作。

### (一) 参数替换
- [x] 使用占位符为 `nGQL | cypher` 替换参数，并执行到数据库;
  - 编写查询脚本模板，搭配参数控制，实现动态查询
  - 通过参数循环，实现批量操作

### (二) 通过 Dao 接口的方法签名信息，对 ResultSet 进行处理，形成业务所需类型
  - [x] 集合类型
      - Collection<基本类型>
      - Collection<对象类型> `Object类型参考下述Object的支持`
  - [x] 基本类型
    - String
    - Boolean
    - Number （Integer、Long、Float、Double、Byte、Short）。**暂时只支持包装类**
  - [x] 对象类型
    -  Object
      - 多列return值转换成 Map
      - 多列return值转换成 POJO
      - 支持Vertex类型转换成 POJO
      - 支持Edge类型转换成 POJO
  - [x] ResultSet 如不需要使用框架自带的结果处理，可直接在接口声明返回值 ResultSet 并自行处理

## 四、主键生成策略接口
- [x] 提供主键生成器的埋点，开发者可自定义主键生成器。
