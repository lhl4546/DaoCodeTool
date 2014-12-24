package org.fire;

/**
 * 
 */

/**
 * mysql类型与java类型映射
 * 
 * @author lhl
 *
 */
public class Type
{
	public static final String VARCHAR = "VARCHAR";
	public static final String CHAR = "CHAR";
	public static final String TEXT = "TEXT";
	public static final String INTEGER = "INT";
	public static final String TINYINT = "TINYINT";
	public static final String BIGINT = "BIGINT";
	public static final String FLOAT = "FLOAT";
	public static final String DOUBLE = "DOUBLE";
	public static final String DECIMAL = "DECIMAL";
	public static final String DATETIME = "DATETIME";
	public static final String BIT = "BIT";
	public static final String SMALLINT = "SMALLINT";
	public static final String MEDIUMBLOB = "MEDIUMBLOB";
	public static final String INTUNSIGNED = "INT UNSIGNED";
	public static final String BIGINTUNSIGNED = "BIGINT UNSIGNED";

	public static final String J_VARCHAR = "String";
	public static final String J_CHAR = "String";
	public static final String J_TEXT = "String";
	public static final String J_INTEGER = "int";
	public static final String J_TINYINT = "int";
	public static final String J_BIGINT = "long";
	public static final String J_FLOAT = "float";
	public static final String J_DOUBLE = "double";
	public static final String J_DECIMAL = "BigDecimal";
	public static final String J_DATETIME = "Date";
	public static final String J_BIT = "boolean";
	public static final String J_SMALLINT = "int";
	public static final String J_MEDIUMBLOB = "String";
	public static final String J_INTUNSIGNED = "long";
	public static final String J_BIGINTUNSIGNED = "long";

	/**
	 * 将mysql类型转换为java类型
	 * 
	 * @param mysqlType
	 *            mysql类型
	 * @return java类型
	 */
	public static String toJavaType(String mysqlType)
	{
		switch (mysqlType)
		{
		case VARCHAR:
			return J_VARCHAR;
		case CHAR:
			return J_CHAR;
		case TEXT:
			return J_TEXT;
		case INTEGER:
			return J_INTEGER;
		case TINYINT:
			return J_TINYINT;
		case BIGINT:
			return J_BIGINT;
		case FLOAT:
			return J_FLOAT;
		case DOUBLE:
			return J_DOUBLE;
		case DECIMAL:
			return J_DECIMAL;
		case DATETIME:
			return J_DATETIME;
		case BIT:
			return J_BIT;
		case SMALLINT:
			return J_SMALLINT;
		case MEDIUMBLOB:
			return J_MEDIUMBLOB;
		case INTUNSIGNED:
			return J_INTUNSIGNED;
		case BIGINTUNSIGNED:
			return J_BIGINTUNSIGNED;
		default:
			return mysqlType;
		}
	}
}
