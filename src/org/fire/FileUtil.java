package org.fire;

/**
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作
 * 
 * @author lhl
 *
 */
public class FileUtil
{
	/**
	 * 将字符串内容保存到指定的文件路径里面
	 * 
	 * @param fileName
	 *            文件路径
	 * @param content
	 *            字符串内容
	 * @throws IOException
	 */
	public static void toFile(String fileName, String content) throws IOException
	{
		File file = new File(fileName);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			bw.write(content);
			bw.flush();
		}
	}

	/**
	 * 将字符串内容追加到文件末尾
	 * 
	 * @param fileName
	 *            文件路径
	 * @param content
	 *            字符串内容
	 * @throws IOException
	 */
	public static void append(String fileName, String content) throws IOException
	{
		File file = new File(fileName);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
		{
			bw.write(content);
			bw.flush();
		}
	}

	/**
	 * 从文件中读取字符串内容并返回
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件字符串格式内容
	 * @throws IOException
	 */
	public static String fromFile(String fileName) throws IOException
	{
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
		{
			String line = null;
			while ((line = br.readLine()) != null)
			{
				content.append(line).append("\r\n");
			}
		}
		return content.toString();
	}

	/**
	 * 按行读取文件内容
	 * 
	 * @param fileName
	 *            文件名(从类路径加载)
	 * @return 以行组织的文件内容
	 * @throws IOException
	 */
	public static List<String> readLines(String fileName) throws IOException
	{
		List<String> lines = new ArrayList<String>();
		try (InputStream in = new FileInputStream(fileName); BufferedReader br = new BufferedReader(new InputStreamReader(in)))
		{
			String line = null;
			while ((line = br.readLine()) != null)
			{
				lines.add(line);
			}
		}
		return lines;
	}
}
