package com.icashflow.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CornJobTest {
    private static String dbURL = "jdbc:mysql://127.0.0.1:3306/filedb";
    private static String dbUser = "root";
    private static String dbPass = "admin";
    
	public static void main(String[] args) {
		
	try {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
	} catch (SQLException | ClassNotFoundException e) {
		e.printStackTrace();
	}
	}

}
