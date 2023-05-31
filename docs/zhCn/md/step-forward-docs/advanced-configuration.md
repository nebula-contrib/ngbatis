
# 进阶配置
## 如何在控制台打印 sql 
```yml
logging:
  level:
    org.nebula.contrib: DEBUG
```

## 如何更改 XXXDao.xml 的位置
```yml
cql:
  parser:
    # 更换开发者自定义的 xml 所在位置
    mapper-locations: ng-mapper/**/*.xml # 默认为 mapper/**/*.xml
```

## 如何修改基类的语句
```yml
cql:
  parser:
    # 如需修改基类的语句，可以指定新文件的位置
    mapper-tpl-location: MyNebulaDaoBasic.xml # 默认为 NebulaDaoBasic.xml
    # 如果对在模板中，不习惯使用 @if 或者 @for 等方式，可以自行更换 定界符
    statement-start: <% # 默认为 @
    statement-end: %> # 默认为 null
```

## 在 xml 写语句时，如果涉及 if 跟 for，又不喜欢用 @if / @for 怎么办
```yml
cql:
  parser:
    # 如果对在模板中，不习惯使用 @if 或者 @for 等方式，可以自行更换 定界符
    statement-start: <% # 默认为 @
    statement-end: %> # 默认为 null
```
