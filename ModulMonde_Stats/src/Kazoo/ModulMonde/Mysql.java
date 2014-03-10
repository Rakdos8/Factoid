package Kazoo.ModulMonde;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Mysql extends Thread{
	
private static Connection con = null;
public static boolean Connected = false;
public static Statement st;
private static String url;
private static String password;
private static String username;

Mysql(String username,String password,String database,String host){
	this.url = "jdbc:mysql://"+host+":3306/"+database;
	this.username= username;
	this.password = password;
	try {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url, username,password);
		st = con.createStatement();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

	 public ResultSet Query(String Query) throws ClassNotFoundException
	 {
		 	try
		 	{
			    ResultSet rs = st.executeQuery(Query);
			    if(rs.isBeforeFirst()) {

				    	//System.out.println("RSData: " + rs.getString(rs.getRow()));
				    	return rs;	
			    }
			    
		 	}catch(SQLException e){
		 	
		 		System.out.println("ERROR_SQL: '"+e+"'");
		 	}
		 	
		 	 return null;
		 
	 }
	 
	 public boolean SearchQuery(String Query) throws ClassNotFoundException
	 {	
		 	try
		 	{
			    ResultSet rs = st.executeQuery(Query);
			    if(rs.next()) {
				    	return true;	
			    }else{
			    	return false;
			    }
			    
		 	}catch(SQLException e){
		 	
		 		System.out.println("ERROR_SQL: '"+e+"'");
		 	}
		 	
		 	
		 	 return false;
		 
	 }
	 
	 public int UpdateQuery(String Query) throws ClassNotFoundException
	 {	
		 	try
		 	{
			    int rs = st.executeUpdate(Query);
			    return rs;
			    
		 	}catch(SQLException e){
		 	
		 		System.out.println("ERROR_SQL: '"+e+"'");
		 	}
		 	
		 	
		 	 return 0;
		 
	 }
	 
	 public void close(){
		 	try {
				  con.close();
			 }catch (SQLException e) {
				 System.out.println("ERROR_SQL: '"+e+"'");
			 }
	 }
		 
}