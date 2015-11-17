####描述
DaoCodeTool是一个用来生成MyBatis数据库操作代码的工具。约定Dao层代码包结构为
```
+entity
+impl
+mapper
+xml
```
其中，`entity`为实体类，类结构与相应数据表一致；`impl`为dao实现类。
####用法
1. 修改`mybatis-config.xml`配置文件，一般只需修改数据库访问参数。
2. 修改`conf.properties`配置文件，可以根据自己的package声明配置，同时配置需要生成代码的数据库(多个数据库用英文逗号分隔)