package com.firecode.hadooptest.hive;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.DriverManager;

/**
 * @author JIANG
 */
public class JDBCTest {
	
	private static final String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
            
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_NAME);
        Connection con = DriverManager.getConnection("jdbc:hive2://192.168.229.130:10001/test", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from person");
        ResultSetMetaData rsmd = rs.getMetaData();
        int count= rsmd.getColumnCount();
        for(int i=0;i<count;i++) {
        	//System.out.print(rsmd.getColumnName(i+1)+"     ");
        }
        //System.out.println();
        while (rs.next()) {
            for(int i=0;i<count;i++) {
            	System.err.print(rs.getString(i+1)+"     ");
            }
            System.err.println();
        }
	}
}
