package me.jume.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
	/**
	 * ��ƴ�ӵõ���JavaBean���ַ���contentд����file�ļ���
	 * @param file
	 * @param content
	 * @throws IOException 
	 */
	public static void writeStr2File(File file,String content) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
	}
}
