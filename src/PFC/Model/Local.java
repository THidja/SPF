package PFC.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Local {
	
	private StringProperty identifiant;
	private IntegerProperty Capacite;
	private static Connection connection = BDDConnector.getInstance();
	
	
	public Local(String identifiant,int Capacite)
	{
		this.identifiant = new SimpleStringProperty(identifiant);
		this.Capacite = new SimpleIntegerProperty(Capacite);
		
	}
	
	public Local()
	{
		this.identifiant = new SimpleStringProperty();
		this.Capacite = new SimpleIntegerProperty();
	}
	
	//
	public String getIdentifiant() 
	{
		return identifiant.get();
	}
	public void setIdentifiant(String identifiant)
	{
		this.identifiant.set(identifiant);
	}
	public StringProperty identifiantProperty()
	{
		return identifiant;
	}
	//
	public int getCapacite() 
	{
		return Capacite.get();
	}
	public void setCapacite(int Capacite) 
	{
		this.Capacite.set(Capacite);
	}
	public IntegerProperty CapaciteProperty()
	{
		return Capacite;
	}
	
	// DataBase Operations
	
	public static void ajouter(Local l) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into local values(?,?)");
		ps.setString(1,l.getIdentifiant());
		ps.setInt(2,l.getCapacite());
		ps.executeUpdate();
		ps.close();
	}
	
	public static ObservableList<Local> consulter() throws SQLException
	{
		ObservableList<Local> listLocal = FXCollections.observableArrayList();
		Statement state;
		state = connection.createStatement();
		ResultSet result = state.executeQuery("select * from local");
		Local l;
		while(result.next())
		{
			l = new Local();
			l.setIdentifiant(result.getString("idLocal"));
			l.setCapacite(result.getInt("capacite"));
			listLocal.add(l);
		}
		state.close();
		return listLocal;	
	}
	
	public static void supprimer(String idLocal) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("delete from local where idLocal = ?");
		ps.setString(1,idLocal);
		ps.executeUpdate();
		ps.close();
	}
	
	public static void modifier(Local l1,Local l2) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("update local set idLocal = ? , capacite = ? where idLocal = ?");
		ps.setString(1,l2.getIdentifiant());
		ps.setInt(2,l2.getCapacite());
		ps.setString(3,l1.getIdentifiant());
		ps.executeUpdate();
		ps.close();
	}
	
    public static Local asObject(String idLocal) throws SQLException
    {
    	PreparedStatement ps = connection.prepareStatement("select * from local where idLocal = ?");
    	ps.setString(1,idLocal);
    	ResultSet result = ps.executeQuery();
    	if(result.first())
    	{
    		Local l = new Local();
    		l.setIdentifiant(idLocal);
    		l.setCapacite(result.getInt("capacite"));
    		return l;
    	}
    	return null;
    }
	
	public String toString()
	{
		return this.getIdentifiant();
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Local)) return false;
		Local l = (Local) o;
		if(l.getIdentifiant().equals(this.getIdentifiant())) return true;
		else return false;
	}
	
}
