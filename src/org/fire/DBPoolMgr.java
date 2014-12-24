package org.fire;

/**
 * 
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 数据库连接池管理器
 * 
 * @author lhl
 *
 */
public class DBPoolMgr
{
	/**
	 * 游戏库
	 */
	private SqlSessionFactory gameSqlSessionFactory;

	/**
	 * 日志库
	 */
	private SqlSessionFactory logSqlSessionFactory;

	/**
	 * 初始化MyBatis
	 */
	public void init()
	{
		initDataBase("mybatis-config.xml");
	}

	/**
	 * 初始化数据库连接池
	 * 
	 * @param mybatisConfigFileName
	 *            mybatis配置文件路径(从类路径加载)
	 */
	public void initDataBase(String mybatisConfigFileName)
	{
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(mybatisConfigFileName);
			gameSqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "game");

			inputStream = new FileInputStream(mybatisConfigFileName);
			logSqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "log");
		}
		catch (IOException e)
		{
			throw new RuntimeException("初始化MyBatis失败", e);
		}
	}

	/**
	 * 获取游戏库连接工厂类
	 * 
	 * @return 游戏库连接工厂类
	 */
	public SqlSessionFactory getGameSqlSessionFactory()
	{
		return gameSqlSessionFactory;
	}

	/**
	 * 获取日志库连接工厂类
	 * 
	 * @return 日志库连接工厂类
	 */
	public SqlSessionFactory getLogSqlSessionFactory()
	{
		return logSqlSessionFactory;
	}
}
