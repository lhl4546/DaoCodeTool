package org.fire;

/**
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Dao文件生成器
 * 
 * @author lhl
 *
 */
public class CodeGenerator
{
	// xml模板文件路径
	private String templateXmlFile = ".\\TemplateMapper_xml";
	// xml生成文件路径
	private String xmlFilePath = ".\\src-gen\\org\\fire\\dao\\xml\\";
	// map模板文件路径
	private String templateMapperFile = ".\\TemplateMapper_java";
	// map生成文件路径
	private String mapperFilePath = ".\\src-gen\\org\\fire\\dao\\mapper\\";
	// dao实现模板文件路径
	private String templateImplFile = ".\\TemplateDaoImpl_java";
	// dao实现生成文件路径
	private String implFilePath = ".\\src-gen\\org\\fire\\dao\\impl\\";
	// entity生成文件路径
	private String javaFilePath = ".\\src-gen\\org\\fire\\dao\\entity\\";
	// entity package声明
	private String packageName;
	// impl package
	private String packageName2;
	// mapper package
	private String packageName3;
	private String entityImport;
	// 数据库名
	private String[] databases;

	public static void main(String[] args) throws SQLException, IOException
	{
		CodeGenerator gen = new CodeGenerator();
		gen.loadConf();
		DBPoolMgr dbm = new DBPoolMgr();
		dbm.init(gen.databases);
		
		List<SqlSessionFactory> sessFacs = dbm.getAllSessionFactory();
		for (SqlSessionFactory sessFac : sessFacs)
		{
			gen.generateXMLFile(sessFac);
			gen.generateMapperFile(sessFac);
			gen.generateImplFile(sessFac);
			gen.generateEntity(sessFac);
		}
	}

	/**
	 * 加载配置文件
	 * 
	 * @throws IOException
	 */
	public void loadConf() throws IOException
	{
		Properties prop = new Properties();
		try (InputStream in = new FileInputStream("conf.properties"))
		{
			prop.load(in);
		}
		packageName = prop.getProperty("entity_package");
		packageName2 = prop.getProperty("impl_package");
		packageName3 = prop.getProperty("mapper_package");
		entityImport = prop.getProperty("entity_import");
		
		String dbNames = prop.getProperty("databases");
		databases = dbNames.split(",");
	}

	/**
	 * 生成xml文件
	 * 
	 * @param sessionFactory
	 * @throws SQLException
	 * @throws IOException
	 */
	public void generateXMLFile(SqlSessionFactory sessionFactory) throws SQLException, IOException
	{
		Connection conn = sessionFactory.openSession().getConnection();
		DatabaseMetaData mData = conn.getMetaData();
		ResultSet tables = mData.getTables(null, null, null, null);

		while (tables.next())
		{
			String tableName = tables.getString("TABLE_NAME");
			ResultSet tableField = mData.getColumns(null, null, tableName, null);

			List<String> type = new ArrayList<String>();
			List<String> fields = new ArrayList<String>();

			while (tableField.next())
			{
				String fieldName = tableField.getString("COLUMN_NAME");
				String fieldType = tableField.getString("TYPE_NAME");
				type.add(fieldType);
				fields.add(fieldName);
			}

			String className = getEntityName(tableName);
			String mapperName = getMapperClassName(className);

			String fields1 = "";
			String fields2 = "";
			String fields3 = "";
			String fields4 = "";
			String fields5 = "";
			String key = fields.get(0);
			String key2 = fields.get(1);
			for (int i = 0; i < fields.size(); i++)
			{
				String field = fields.get(i);
				fields1 += field;
				fields2 += "#{item." + field + "}";
				fields3 += "#{" + field + "}";
				fields4 += field + " = #{" + field + "}";
				fields5 += field + " =  values(" + field + ")";

				if (i < (fields.size() - 1))
				{
					fields1 += ", ";
					fields2 += ", ";
					fields3 += ", ";
					fields4 += ", ";
					fields5 += ", ";
				}
			}

			String content = FileUtil.fromFile(templateXmlFile);
			content = content.replace("?mappername?", mapperName);
			content = content.replace("?tablename?", tableName);
			content = content.replace("?classname?", className);
			content = content.replace("?fields1?", fields1);
			content = content.replace("?fields2?", fields2);
			content = content.replace("?fields3?", fields3);
			content = content.replace("?fields4?", fields4);
			content = content.replace("?fields5?", fields5);
			content = content.replace("?key?", key);
			content = content.replace("?key2?", key2);

			String fileName = getFileName(xmlFilePath, mapperName, ".xml");
			FileUtil.toFile(fileName, content);
		}
	}

	public ResultSet resultSet(SqlSessionFactory sessionFactory) throws SQLException
	{
		Connection conn = sessionFactory.openSession().getConnection();
		DatabaseMetaData mData = conn.getMetaData();
		return mData.getTables(null, null, null, null);
	}

	/**
	 * 生成map文件
	 * 
	 * @param sessionFactory
	 * @throws SQLException
	 * @throws IOException
	 */
	public void generateMapperFile(SqlSessionFactory sessionFactory) throws SQLException, IOException
	{
		ResultSet tables = resultSet(sessionFactory);

		while (tables.next())
		{
			String tableName = tables.getString("TABLE_NAME");

			String className = getEntityName(tableName);
			String mapperName = getMapperClassName(className);

			String content = FileUtil.fromFile(templateMapperFile);
			content = content.replace("?classname?", className);
			content = content.replace("?mappername?", mapperName);

			String packageName = makePackage(packageName3);
			content = packageName + content;

			String fileName = getFileName(mapperFilePath, mapperName, ".java");
			FileUtil.toFile(fileName, content);
		}
	}

	/**
	 * 生成dao实现文件
	 * 
	 * @param sessionFactory
	 * @throws SQLException
	 * @throws IOException
	 */
	public void generateImplFile(SqlSessionFactory sessionFactory) throws SQLException, IOException
	{
		ResultSet tables = resultSet(sessionFactory);

		while (tables.next())
		{
			String tableName = tables.getString("TABLE_NAME");

			String className = getEntityName(tableName);
			String daoName = getDaoClassName(className);
			String mapperName = getMapperClassName(className);
			String dbName = tableName.startsWith("t_log") ? "Log" : "Game";

			String content = FileUtil.fromFile(templateImplFile);
			content = content.replace("?classname?", className);
			content = content.replace("?mappername?", mapperName);
			content = content.replace("?daoname?", daoName);
			content = content.replace("?dbname?", dbName);

			String packageName = makePackage(packageName2);
			content = packageName + content;

			String fileName = getFileName(implFilePath, daoName, ".java");
			FileUtil.toFile(fileName, content);
		}
	}

	/**
	 * 生成实体类
	 * 
	 * @param sessionFactory
	 * @throws SQLException
	 * @throws IOException
	 */
	public void generateEntity(SqlSessionFactory sessionFactory) throws SQLException, IOException
	{
		Connection conn = sessionFactory.openSession().getConnection();
		DatabaseMetaData mData = conn.getMetaData();
		ResultSet tables = mData.getTables(null, null, null, null);

		while (tables.next())
		{
			String tableName = tables.getString("TABLE_NAME");
			ResultSet fields = mData.getColumns(null, null, tableName, null);

			List<String> type = new ArrayList<String>();
			List<String> field = new ArrayList<String>();
			List<String> comment = new ArrayList<String>();

			while (fields.next())
			{
				String fieldName = fields.getString("COLUMN_NAME");
				String fieldComment = fields.getString("REMARKS");
				String fieldType = fields.getString("TYPE_NAME");
				type.add(fieldType);
				field.add(fieldName);
				comment.add(fieldComment);
			}
			String className = getEntityName(tableName);
			makeJavaFile(className, type, field, comment);
		}
	}

	public String makePackage(String packageName)
	{
		return packageName + Constants.NEW_LINE_2;
	}

	/**
	 * 生成import语句，根据字段类型生成需要的import语句
	 * 
	 * @param types
	 *            字段类型
	 * @return import语句
	 */
	public String makeImport(List<String> types)
	{
		Set<String> mutex = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		for (String type : types)
		{
			if (!mutex.add(type))
			{
				continue;
			}
			switch (type)
			{
			case Type.DATETIME:
			{
				builder.append("import java.util.Date;").append(Constants.NEW_LINE);
				break;
			}
			case Type.DECIMAL:
			{
				builder.append("import java.math.BigDecimal;").append(Constants.NEW_LINE);
				break;
			}
			default:
				break;
			}
		}
		builder.append(entityImport).append(Constants.NEW_LINE);
		builder.append(Constants.NEW_LINE);
		return builder.toString();
	}

	/**
	 * 一般用来构造构造函数
	 * 
	 * @param className
	 *            类名
	 * @param types
	 *            字段类型
	 * @param fields
	 *            字段名
	 * @return 拼接好的构造函数代码
	 */
	public String makeConstructor(String className, List<String> types, List<String> fields)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("public ").append(className).append(" ()").append(Constants.NEW_LINE);
		builder.append("{").append(Constants.NEW_LINE);
		builder.append("}").append(Constants.NEW_LINE);
		builder.append(Constants.NEW_LINE);

		builder.append("public ").append(className).append(" (");
		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i);
			type = Type.toJavaType(type);
			String field = fields.get(i);
			builder.append(type).append(Constants.SPACE).append(field);
			if (i != types.size() - 1)
			{
				builder.append(", ");
			}
		}
		builder.append(")").append(Constants.NEW_LINE);
		builder.append("{").append(Constants.NEW_LINE);
		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i);
			type = Type.toJavaType(type);
			String field = fields.get(i);
			builder.append("this.").append(field).append(" = ").append(field).append(";").append(Constants.NEW_LINE);
		}
		builder.append("}");
		builder.append(Constants.NEW_LINE);

		return builder.toString();
	}

	/**
	 * 一般用来构造get和set方法、字段说明、toString方法
	 * 
	 * @param types
	 *            字段类型
	 * @param fields
	 *            字段名
	 * @return 拼接好的get和set或字段说明或toString代码
	 */
	public String makeFields(List<String> types, List<String> fields, List<String> comments)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i);
			type = Type.toJavaType(type);
			String field = fields.get(i);
			String comment = comments.get(i);
			builder.append("/**").append(Constants.NEW_LINE);
			builder.append("* ").append(comment).append(Constants.NEW_LINE);
			builder.append("*/").append(Constants.NEW_LINE);
			builder.append("private ").append(type).append(Constants.SPACE).append(field).append(";");
			builder.append(Constants.NEW_LINE);
		}
		return builder.toString();
	}

	/**
	 * 一般用来构造get和set方法、字段说明、toString方法
	 * 
	 * @param types
	 *            字段类型
	 * @param fields
	 *            字段名
	 * @return 拼接好的get和set或字段说明或toString代码
	 */
	public String makeGetterAndSetter(List<String> types, List<String> fields, List<String> comments)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i);
			type = Type.toJavaType(type);
			String field = fields.get(i);
			StringBuilder sb = new StringBuilder(field);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			String field2 = sb.toString();
			String comment = comments.get(i);
			builder.append("/**").append(Constants.NEW_LINE);
			builder.append("* ").append(comment).append(Constants.NEW_LINE);
			builder.append("*/").append(Constants.NEW_LINE);
			builder.append("public ").append(type).append(" get").append(field2).append(" ()").append(Constants.NEW_LINE);
			builder.append("{").append(Constants.NEW_LINE);
			builder.append("return ").append(field).append(";").append(Constants.NEW_LINE);
			builder.append("}").append(Constants.NEW_LINE);
			builder.append(Constants.NEW_LINE);

			builder.append("/**").append(Constants.NEW_LINE);
			builder.append("* ").append(comment).append(Constants.NEW_LINE);
			builder.append("*/").append(Constants.NEW_LINE);
			builder.append("public void ").append("set").append(field2).append(" (").append(type).append(Constants.SPACE).append(field).append(")").append(Constants.NEW_LINE);
			builder.append("{").append(Constants.NEW_LINE);
			builder.append("this.").append(field).append(" = ").append(field).append(";").append(Constants.NEW_LINE);
			builder.append("}").append(Constants.NEW_LINE);
			builder.append(Constants.NEW_LINE);
		}
		return builder.toString();
	}

	/**
	 * 处理代码并保存至类文件
	 * 
	 * @param className
	 *            类名
	 * @param types
	 *            字段类型
	 * @param fields
	 *            字段
	 * @param comments
	 *            字段注释
	 * @throws IOException
	 */
	public void makeJavaFile(String className, List<String> types, List<String> fields, List<String> comments) throws IOException
	{
		String packages = makePackage(packageName);
		String imports = makeImport(types);
		String con = makeConstructor(className, types, fields);
		String fie = makeFields(types, fields, comments);
		String gs = makeGetterAndSetter(types, fields, comments);
		StringBuilder builder = new StringBuilder();
		builder.append(packages);
		builder.append(imports);
		builder.append("public class ").append(className).append(" extends DataObject").append(Constants.NEW_LINE);
		builder.append("{").append(Constants.NEW_LINE);
		builder.append(fie).append(Constants.NEW_LINE);
		builder.append(con).append(Constants.NEW_LINE);
		builder.append(gs).append(Constants.NEW_LINE);
		builder.append("}");
		String java = builder.toString();

		String fileName = getFileName(javaFilePath, className, ".java");
		FileUtil.toFile(fileName, java);
	}

	/**
	 * 获取文件保存路径，将dir和参数className拼接成文件保存路径
	 * 
	 * @param dir
	 *            文件路径，不包括文件名
	 * @param fileName
	 *            文件名
	 * @param suffix
	 *            文件后缀
	 * @return 文件路径
	 */
	private static String getFileName(String dir, String fileName, String suffix)
	{
		File file = new File(dir);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return dir + fileName + suffix;
	}

	/**
	 * 获取实体类名 从表名提取
	 * 
	 * @param tableName
	 *            表名
	 * @return 实体类名
	 */
	private String getEntityName(String tableName)
	{
		String[] pairs = tableName.split("_");
		String entityName = "";
		for (int i = 2; i < pairs.length; i++)
		{
			StringBuilder builder = new StringBuilder(pairs[i]);
			builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
			entityName += builder.toString();
		}
		if (tableName.startsWith("t_u"))
		{
			entityName += "Info";
		}
		else if (tableName.startsWith("t_s"))
		{
			entityName += "Bean";
		}
		else if (tableName.startsWith("t_log"))
		{
			entityName += "Log";
		}
		return entityName;
	}

	private String getMapperClassName(String className)
	{
		return className + "Mapper";
	}

	private String getDaoClassName(String className)
	{
		return className + "Dao";
	}
}
