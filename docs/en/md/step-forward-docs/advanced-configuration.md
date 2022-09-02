# Advanced Configuration
## How to display SQL on the console
```yml
logging:
  level:
    org.nebula.contrib: DEBUG
```

## How to change location of XXXDao.xml
```yml
cql:
  parser:
    # Change the location of the XML customized by the developer
    mapper-locations: ng-mapper/**/*.xml # default: mapper/**/*.xml
```

## How to modify the statement of the NebulaBasicDao
```yml
cql:
  parser:
    # To modify the statement of the NebulaBasicDao, you can specify the location of the new file. 
    # The file needs to be created in the resources directory
    mapper-tpl-location: MyNebulaDaoBasic.xml # default: NebulaDaoBasic.xml
```

## When writing statements in XML, what if `if` and `for` are involved and @if / @for is not preferred
```yaml
cql:
  parser:
    # If you are not used to using @if or @for in the template, you can replace the delimiter by yourself
    statement-start: <% # default: @
    statement-end: %> # default: null
```