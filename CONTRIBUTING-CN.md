# 如何贡献代码？

## Markdown 格式检查

提交带有 `.md` 后缀的文件前，可以先进行格式检查。

```bash
npm ci
npm run lint-md
```

如遇到任何格式错误，请在提交前修复。大多数情况下，内置的格式化工具可能会有所帮助。

```bash
npm run format-md
```

## 环境要求

- JDK >= 8
- NebulaGraph > v3.0
- Springboot 2.x
- Maven

## 克隆仓库

1. 在仓库主页右上角fork到自己的仓库
2. 从自己的仓库中clone到本地

## 开发步骤

1. 如果是第一次贡献，请在 pom.xml 添加作者信息
2. 按你的想法开发功能，把好功能固化下来与其他开发者分享
3. 在 `CHANGELOG.md` 中描述新增的功能及使用方式、场景

## 运行测试用例及代码风格检查

1. 在`ngbatis-demo/test`中运行单元测试
2. 使用maven插件做代码风格检查：`maven->plugins->checkstyle->checkstyle:check`

## 提交并发起PR流程

## 体会分享所带来的快乐

> 有任何问题，请放心提issue
