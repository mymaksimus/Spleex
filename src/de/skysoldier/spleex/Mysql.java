package de.skysoldier.spleex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Mysql {
	
	private Connection connection;
	
	public Mysql(String host, String db, int port, String user, String password){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void executeUpdate(String query){
		try{
			PreparedStatement ps = connection.prepareStatement(query);
			ps.executeUpdate();
			ps.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String query){
		try{
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet result = ps.executeQuery();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean next(ResultSet set){
		try{
			return set.next();
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isEmpty(ResultSet set){
		try{
			return !set.isBeforeFirst();
		}
		catch(Exception e){
			e.printStackTrace();
			return true;
		}
	}
	
	
	public String getString(ResultSet set, String columnName){
		try{
			return set.getString(columnName);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}