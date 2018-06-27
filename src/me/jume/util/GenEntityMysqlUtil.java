package me.jume.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;



public class GenEntityMysqlUtil {
	private static String[] fieldNames;		// ���ݿ��ֶ���
	private static String[] colNames;		// JavaBean��������
	private static String[] colTypes;		// ������������
	private static int[] colSizes;			// ������С����
	
	private static boolean f_util = false;		// ��Ҫ����java.util.*;
	private static boolean f_sql = false;		// ��Ҫ����java.sql.*;
	
	private static final String NAME_END = "VO";	// ʵ����������淶����׺
	private static String primaryKey = "";		// ����
	
	static Connection conn = DBUtils.getCon();		// ��ȡ���ݿ������
	
	/**
	 * �������ݿ��еı���tableName����JavaBeanд����packagePath·����
	 * @param packagePath
	 * @param tableName
	 */
	public static void genEntity(String packagePath,String tableName){
		PreparedStatement ps = null;
		String sql = "select * from " + tableName;
		try {
			// ��ȡԪ���ݣ����������ű�ı�ṹ��Ϣ
			DatabaseMetaData dbmd = conn.getMetaData();
			// ��ȡ�����У�����ϵ���ԣ�
			ResultSet primaryKeys =dbmd.getPrimaryKeys(null, null, tableName);
			while (primaryKeys.next()) {
				primaryKey = primaryKeys.getString(4);
				System.out.println("�������ƣ�" + primaryKey);
			}
			primaryKeys.close();
			ps = conn.prepareStatement(sql);
			ResultSetMetaData rsmd = ps.getMetaData();
			int size = rsmd.getColumnCount();		// ��ȡ�ܹ��ж�����
			fieldNames = new String[size];
			colNames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			for(int i=0; i<rsmd.getColumnCount(); i++){
				fieldNames[i] = rsmd.getColumnName(i+1);
				colNames[i] = ExTypeUtils.getCamelStr(fieldNames[i]);
				colTypes[i] = rsmd.getColumnTypeName(i+1);
				if (colTypes[i].equalsIgnoreCase("datetime")) {
					f_util = true;
				}
				if (colTypes[i].equalsIgnoreCase("image")||
						colTypes[i].equalsIgnoreCase("text")) {
					f_sql = true;
				}
				colSizes[i] = rsmd.getColumnDisplaySize(i+1);
			}
			// ��������ȡ������ص����ݿ���ֶκ�����������Ϣ�����н�����ƴ�ӵõ�JavaBean���ַ���
			String content = parse(colNames,colTypes,colSizes,packagePath,tableName);
			System.out.println(content);
			String path = System.getProperty("user.dir") + "/src/" +
					packagePath.replaceAll("\\.", "/");
			File dir = new File(path);		// �ڵ�ǰ��Ŀ��Ӧλ�ô����������ڴ�����ɵ�JavaBean
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// �ڴ��ļ����д���һ��Java�ļ������ҽ��ڴ��ܹ���JavaBean�ַ���д�����ļ���
			String resPath = path + "/" + ExTypeUtils.initcap(tableName)+ NAME_END + ".java";
			FileUtils.writeStr2File(new File(resPath), content);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * ��������ȡ������ص����ݿ���ֶκ�����������Ϣ�����н�����ƴ�ӵõ�JavaBean���ַ���
	 * @param colNames2
	 * @param colTypes2
	 * @param colSizes2
	 * @param packagePath
	 * @param tableName
	 * @return
	 */
	private static String parse(String[] colNames2, String[] colTypes2, int[] colSizes2, String packagePath,
			String tableName) {
		StringBuffer sb = new StringBuffer();
		// �� package ���		���û�ָ���ŵ���Ŀ���ĸ�����
		sb.append("package " + packagePath + ";\r\n\r\n");
		// �� import ���			���л���ע�⡢ʱ��������
		sb.append("import java.io.Serializable;\r\n");
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("import java.sql.*;\r\n");
		}
		sb.append("import javax.persistence.Column;\r\n");
		sb.append("import javax.persistence.Entity;\r\n");
		sb.append("import javax.persistence.Id;\r\n");
		sb.append("import javax.persistence.Table;\r\n");
		// �� �����������ϵ�ע��
		sb.append("@Entity\r\n");
		sb.append("@Table(name=\"").append(tableName).append("\")\r\n");
		sb.append("public class ").append(ExTypeUtils.initcap(tableName));
		sb.append(NAME_END).append(" implements Serializable {\r\n\r\n");
		
		// �� ���ڲ��ĳ�Ա������getXXX��setXXX
		processAllAttrs(sb);		// �����������Բ�׷�ӵ�sb��
		sb.append("\r\n");
		processAllMethods(sb);
		sb.append("}\r\n");
		return sb.toString();
	}

	// ��������get��set������׷�ӵ�sb��
	private static void processAllMethods(StringBuffer sb) {
		for (int i = 0; i < colNames.length; i++) {
			// ���ע��
			if(primaryKey.equals(fieldNames[i])){
				sb.append("\t@Id").append("\r\n");
			}
			sb.append("\t@Column(name = \"").append(fieldNames[i]).append("\")").append("\r\n");
			
			sb.append("\tpublic " + ExTypeUtils.toJavaType(colTypes[i]) + " get"
					+ ExTypeUtils.initcap(colNames[i]) + "(){\r\n");
			sb.append("\t\treturn " + colNames[i] + ";\r\n");
			sb.append("\t}\r\n");
			
			sb.append("\tpublic void set" + ExTypeUtils.initcap(colNames[i]) + "("
					+ ExTypeUtils.toJavaType(colTypes[i]) + " " + colNames[i]
					+ "){\r\n");
			sb.append("\t\tthis." + colNames[i] + " = " + colNames[i] + ";\r\n");
			sb.append("\t}\r\n\r\n");
		}
	}

	// �����������Բ�׷�ӵ�sb��
	private static void processAllAttrs(StringBuffer sb) {
		for(int i=0; i<colNames.length; i++){
			sb.append("\tprivate ")
			.append(ExTypeUtils.toJavaType(colTypes[i]))
			.append(" " + colNames[i] + ";\r\n");
		}
	}
}
