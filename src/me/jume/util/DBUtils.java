package me.jume.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

// ���ݿ����ӹ����࣬��ȡ���ݿ�����
public class DBUtils {
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	private static Connection con;
	static{
		ResourceBundle rsb = ResourceBundle.getBundle("db");
		driver = rsb.getString("driver");
		url = rsb.getString("url");
		user = rsb.getString("user");
		pwd = rsb.getString("pwd");
	}
	public static Connection getCon(){
		try {
			Class.forName(driver);
			// ��2����ȡ֮ǰ�����ж��Ƿ��Ѿ��ж�Ӧ��ʵ�������û�����»�ȡһ��
			if(con == null){
				con = DriverManager.getConnection(url, user, pwd);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	public static void main(String[] args) {
		System.out.println(DBUtils.getCon());
	}
	public static void closeAll(ResultSet rs, PreparedStatement ps, Connection con){
		try{
			if(rs != null ) rs.close(); 
			if(ps != null ) ps.close(); 
			if(con != null ) con.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
