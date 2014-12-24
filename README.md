#DaoCodeTool
这是一个用来生成MyBatis数据库访问层代码的工具。约定代码目录为entity + impl + mapper + xml结构，其中entity为实体类包，
用于存放数据表映射实体类；impl为MyBatis数据库操作实现类包，impl类实现一个通用接口BaseDao；mapper为MyBatis映射类包；
xml为MyBatis具体SQL语句配置包。
生成结构示例：
src-gen
	org.fire.dao.entity
	org.fire.dao.impl
	org.fire.dao.mapper
	org.fire.dao.xml
用法：
    1.打开工程，修改mybatis-config.xml文件，只需要修改environment配置以使MyBatis能正常访问数据库
    2.修改conf.properties文件，以使生成代码使用自己的package声明和import语句
    3.运行CodeGenerator类即可在工程目录的src-gen目录下生成对应的代码