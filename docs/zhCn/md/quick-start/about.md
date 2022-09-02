[![LOGO](./light.png)](https://github.com/nebula-contrib/ngbatis)

# 关于 Ngbatis

## 应用程序中的定位
**NGBATIS** 是一款针对 [Nebula Graph](https://github.com/vesoft-inc/nebula) + Springboot 的数据库 ORM 框架。借鉴于 [MyBatis](https://github.com/mybatis/mybatis-3) 的使用习惯进行开发。包含了一些类似于[mybatis-plus](https://github.com/baomidou/mybatis-plus)的单表操作，另外还有一些图特有的实体-关系基本操作。  
如果使用上更习惯于JPA的方式，[graph-ocean](https://github.com/nebula-contrib/graph-ocean) 是个不错的选择。

> 如果你还没有属于自己的 Nebula 图数据库，请给自己安排上。[安装传送门](https://docs.nebula-graph.com.cn/3.2.0/4.deployment-and-installation/2.compile-and-install-nebula-graph/3.deploy-nebula-graph-with-docker-compose/)

## 关键环节的相关技术
- 数据库：[Nebula Graph](https://github.com/vesoft-inc/nebula)
- 动态代理生成框架：[ASM](https://gitlab.ow2.org/asm/asm/) `v8.0`
- mapper文件解析：[jsoup](https://github.com/jhy/jsoup) `v1.12.1`
- nGQL模板：[Beetl](https://github.com/javamonkey/beetl2.0) `v3.1.8.RELEASE`

## 环境要求
- Java 8+
- Springboot