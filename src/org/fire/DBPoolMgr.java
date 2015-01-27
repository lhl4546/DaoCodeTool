package org.fire;

/**
 * 
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
	private List<SqlSessionFactory> sqlSessionFactorys = new ArrayList<SqlSessionFactory>();

	/**
	 * 初始化MyBatis
	 * 
	 * @param databases
	 *            数据库名
	 */
	public void init(String[] databases)
	{
		initDataBase("mybatis-config.xml", databases);
	}

	/**
	 * 初始化数据库连接池
	 * 
	 * @param mybatisConfigFileName
	 *            mybatis配置文件路径
	 * @param databases
	 *            数据库名
	 */
	public void initDataBase(String mybatisConfigFileName, String[] databases)
	{
		if (databases == null || databases.length == 0) return;
		InputStream inputStream = null;
        
        for (String db : databases)
        {
            try (InputStream in = new FileInputStream(mybatisConfigFileName)
            {
                sqlSessionFactorys.add(new SqlSessionFactoryBuilder().build(in, db));
            }
            catch (IOException e)
		    {
		    	throw new RuntimeException("初始化 [" + db + "] 失败", e);
		    }
        }
	}

	/**
	 * 获取配置的数据库连接
	 * 
	 * @return
	 */
	public List<SqlSessionFactory> getAllSessionFactory()
	{
		return sqlSessionFactorys;
	}
}