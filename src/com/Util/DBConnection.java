package com.Util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBConnection {
	static SessionFactory sf = null;
	Session session = null;
	Connection conn = null;
	
	private static SessionFactory sessionFactory = openConnection();
	
	public static SessionFactory openConnection(){
		try{
				sf = new Configuration()
					.addAnnotatedClass(com.Model.DiseaseModel.class)
					.configure()
					.buildSessionFactory();
				System.out.println("Session Factory Created");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sf;
	}
	public Session getConnection(){
		try{
			if(sessionFactory==null){
				sessionFactory = openConnection();
			}
			session = sessionFactory.openSession();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}
	public Connection getJDBCConnection(){
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sl-dss?useSSL=false","root","root");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
