package PFC.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import PFC.Main;



public abstract class BDDConnector {
	
	private static Connection connection;
	
	public static Connection getInstance()
	{
		if(connection == null)
		{
			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3319/EP","root","pfc_admin");
			} 
			catch (SQLException e)
			{
				Main.showExceptionDialog(e);
			}
		}
		
		return connection;
	}

}