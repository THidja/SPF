package PFC.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Enseignant {

	private StringProperty Nom;
	private StringProperty Prenom;
	private StringProperty Grade;
	private static Connection connection = BDDConnector.getInstance();
	
	public Enseignant(String Nom,String Prenom , String Grade)
	{
		this.Nom = new SimpleStringProperty(Nom);
		this.Prenom = new SimpleStringProperty(Prenom);
		this.Grade = new SimpleStringProperty(Grade);
	}
	
	public Enseignant()
	{
		this.Nom = new SimpleStringProperty();
        this.Prenom = new SimpleStringProperty();
		this.Grade = new SimpleStringProperty();
	}
	
	// 
	public String getNom() 
	{
		return Nom.get();
	}
	public void setNom(String Nom)
	{
		this.Nom.set(Nom);
	}
	public StringProperty NomProperty()
	{
		return Nom;
	}
	//
	public String getPrenom() 
	{
		return Prenom.get();
	}
	public void setPrenom(String Prenom)
	{
		this.Prenom.set(Prenom);
	}
	public StringProperty PrenomProperty()
	{
		return Prenom;
	}
	//
	public String getGrade() 
	{
		return Grade.get();
	}
	public void setGrade(String Grade)
	{
		this.Grade.set(Grade);
	}
	public StringProperty GradeProperty()
	{
		return Grade;
	}
	
	// Database Operations
	
	public static void ajouter(Enseignant e) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into enseignant values(?,?,?)");
		ps.setString(1,e.getNom());
		ps.setString(2,e.getPrenom());
		ps.setString(3,e.getGrade());
		ps.executeUpdate();
		ps.close();
	}
	
	public static  ObservableList<Enseignant> consulter() throws SQLException
	{
		ObservableList<Enseignant> listEnseignants = FXCollections.observableArrayList();
	    Statement state = connection.createStatement();
	    ResultSet result = state.executeQuery("select * from enseignant");
		Enseignant e;
		while(result.next())
		{
			e = new Enseignant();
			e.setNom(result.getString("nom"));
			e.setPrenom(result.getString("prenom"));
			e.setGrade(result.getString("grade"));
			listEnseignants.add(e);
		}
		state.close();
		return listEnseignants;
	}
	
	public static void supprimer(Enseignant e) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("delete from enseignant where nom = ? and prenom = ?");
		ps.setString(1,e.getNom());
		ps.setString(2,e.getPrenom());
		ps.executeUpdate();
		ps.close();
	}
	
	public static void modifier(Enseignant e1,Enseignant e2) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("update enseignant set nom = ? , prenom = ? , grade = ? "
				                                                                               	+ " where nom = ? and prenom = ? ");
		ps.setString(1,e2.getNom());
		ps.setString(2,e2.getPrenom());
		ps.setString(3,e2.getGrade());
		ps.setString(4,e1.getNom());
		ps.setString(5,e1.getPrenom());
		ps.executeUpdate();
		ps.close();
	}
	
	public static Enseignant asObejct(String nom,String prenom) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("select * from enseignant where nom = ? and prenom = ?");
		ps.setString(1,nom);
		ps.setString(2,prenom);
		ResultSet result = ps.executeQuery();
		if(result.first())
		{
			Enseignant enseignant = new Enseignant();
			enseignant.setNom(nom);
			enseignant.setPrenom(prenom);
			enseignant.setGrade(result.getString("grade"));
		    return enseignant;
		}
		return null;
	}
	
	public String toString()
	{
		return this.getNom()+" "+this.getPrenom();
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Enseignant)) return false;
		Enseignant e = (Enseignant) o;
		if(e.getNom().equals(this.getNom()) && e.getPrenom().equals(this.getPrenom())) return true;
		else return false;
	}

	
}