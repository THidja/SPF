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

public class Promotion {
	
	private StringProperty identifiant;
	private IntegerProperty nbSec;
	private IntegerProperty nbGroupes;
	private IntegerProperty tailleG;
	private StringProperty semestre1;
	private StringProperty semestre2;
	private ObservableList<Module> modulesList = FXCollections.observableArrayList();
	private static Connection connection = BDDConnector.getInstance();
	
	
	public Promotion(String identifiant,int nbSec,int nbGroupes,int tailleG,String semestre1,String semestre2)
	{
		this.identifiant = new SimpleStringProperty(identifiant);
		this.nbSec = new SimpleIntegerProperty(nbSec);
		this.nbGroupes = new SimpleIntegerProperty(nbGroupes);
		this.tailleG = new SimpleIntegerProperty(tailleG);
		this.semestre1 = new SimpleStringProperty(semestre1);
		this.semestre2 = new SimpleStringProperty(semestre2);
	}
	
	public Promotion()
	{
		this.identifiant = new SimpleStringProperty();
		this.nbSec = new SimpleIntegerProperty();
		this.nbGroupes = new SimpleIntegerProperty();
		this.tailleG = new SimpleIntegerProperty();
		this.semestre1 = new SimpleStringProperty();
		this.semestre2 = new SimpleStringProperty();
	}
	
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
	public int getNbSec() 
	{
		return nbSec.get();
	}
	public void setNbSec(int nbSec) 
	{
		this.nbSec.set(nbSec);
	}
	public IntegerProperty nbSecProperty()
	{
		return nbSec;
	}
	//
	public int getNbGroupes() 
	{
		return nbGroupes.get();
	}
	public void setNbGroupes(int nbGroupes)
	{
		this.nbGroupes.set(nbGroupes);
	}
	public IntegerProperty nbGroupesProperty()
	{
		return nbGroupes;
	}
	//
	public int getTailleG()
	{
		return tailleG.get();
	}
	public void setTailleG(int tailleG)
	{
		this.tailleG.set(tailleG);
	}
	public IntegerProperty tailleGProperty()
	{
		return tailleG;
	}
	//
	public String getSemestre1()
	{
		return semestre1.get();
	}
	public void setSemestre1(String semestre1)
	{
		this.semestre1.set(semestre1);
	}
	public StringProperty semestre1Property()
	{
		return semestre1;
	}
	//
	public String getSemestre2()
	{
		return semestre2.get();
	}
	public void setSemstre2(String semestre2)
	{
		this.semestre2.set(semestre2);
	}
	public StringProperty semestre2Property()
	{
		return semestre2;
	}
	//
	public ObservableList<Module> getModulesList()
	{
		return modulesList;
	}
	
	public void setModulesList(ObservableList<Module> modulesList)
	{
		this.modulesList = modulesList;
	}
	
	// DataBase Operations 
	
	public static void ajouter(Promotion p) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into promotion values(?,?,?,?)");
		ps.setString(1,p.getIdentifiant());
		ps.setInt(2,p.getNbSec());
		ps.setInt(3,p.getNbGroupes());
		ps.setInt(4,p.getTailleG());
		ps.executeUpdate();
		ps.close();
		
		ps = connection.prepareStatement("insert into semestre values(?,?),(?,?)");
		ps.setString(1,p.getSemestre1());
		ps.setString(2,p.getIdentifiant());
		ps.setString(3,p.getSemestre2());
		ps.setString(4,p.getIdentifiant());
		ps.executeUpdate();
		ps.close();
	}
	
	public static void supprimer(Promotion p) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("delete from promotion where idPromotion = ?");
		ps.setString(1,p.getIdentifiant());
		ps.executeUpdate();
		ps.close();
	}
	
	public static ObservableList<Promotion> consulter() throws SQLException
	{
		ObservableList<Promotion> listPromotions = FXCollections.observableArrayList();
		Statement state = connection.createStatement();
		ResultSet result = state.executeQuery("select * from promotion");
		Promotion p;
		while(result.next())
		{
			p = new Promotion();
			p.setIdentifiant(result.getString("idPromotion"));
			p.setNbSec(result.getInt("nbSections"));
			p.setNbGroupes(result.getInt("nbGroupes"));
			p.setModulesList(Module.consulter(p,null));
			listPromotions.add(p);
		}
		state.close();
		return listPromotions;	
	}
	
	public static void modifier(Promotion p1,Promotion p2) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("update promotion set idPromotion = ?, nbSections = ? , nbGroupes = ?"
															                                        +" where idPromotion = ?");
		ps.setString(1,p2.getIdentifiant());
		ps.setInt(2,p2.getNbSec());
		ps.setInt(3,p2.getNbGroupes());
		ps.setString(4,p1.getIdentifiant());
		ps.executeUpdate();
		ps.close();
	}
	
	public Promotion asObject(String idPromotion) throws SQLException 
	{
		Promotion promotion = new Promotion();
		PreparedStatement ps = connection.prepareStatement("select * from promotion where idPromotion = ?");
		ps.setString(1,idPromotion);
		ResultSet result = ps.executeQuery();
		if(result.first())
		{
			promotion.setIdentifiant(idPromotion);
			promotion.setNbGroupes(result.getInt("nombreS"));
			promotion.setNbSec(result.getInt("nombreG"));
		}
		return promotion;
	}
	
	public String toString()
	{
		return identifiant.get();
	}
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Promotion)) return false;
	    Promotion p = (Promotion) o;
		if(p.getIdentifiant().equals(this.getIdentifiant())) return true;
		else return false;
	}
	public Promotion clone()
	{
		Promotion p = new Promotion(this.getIdentifiant(),this.getNbSec(),this.getNbGroupes(),this.getTailleG(),
										this.getSemestre1(),this.getSemestre2());
		p.getModulesList().addAll(this.getModulesList());
		return p;
	}

}
