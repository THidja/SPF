package PFC.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Module {
	private StringProperty intitule;
	private StringProperty semestre;
	private ObservableList<Enseignant> listEnseignant = FXCollections.observableArrayList();
	
	
	private static Connection connection = BDDConnector.getInstance();
	public Module()
	{
		this.intitule = new SimpleStringProperty();
		this.semestre = new SimpleStringProperty();
	}
	public Module(String intitule,String semestre)
	{
		this.intitule = new SimpleStringProperty(intitule);
		this.semestre = new SimpleStringProperty(semestre);
	}

	public String getIntitule()
	{
		return intitule.get();
	}
	public void setItitule(String intitule)
	{
		this.intitule.set(intitule);
	}
	public StringProperty intituleProperty()
	{
		return intitule;
	}
	
	public String getSemestre()
	{
		return semestre.get();
	}
	public void setSemestre(String semestre)
	{
		this.semestre.set(semestre);
	}
	public StringProperty semestreProperty()
	{
		return semestre;
	}
	
	public ObservableList<Enseignant> getListEnseignant()
	{
		return listEnseignant;
	}
	
	public void setListEnseignant(ObservableList<Enseignant> listEnseignant)
	{
		this.listEnseignant = listEnseignant;
	}
	
	// DataBase Operations 
	
	public static void ajouter(Module m,Promotion p) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into module values(?,?,?)");
		ps.setString(1,m.getIntitule());
		ps.setString(2,p.getIdentifiant());
		ps.setString(3,m.getSemestre());
		ps.executeUpdate();
		ps.close();
		ps = connection.prepareStatement("insert into assurer values(?,?,?)");
		ps.setString(3,m.getIntitule());
		for(Enseignant e : m.getListEnseignant())
		{
			ps.setString(1,e.getNom());
			ps.setString(2,e.getPrenom());
			ps.executeUpdate();
		}
		ps.close();
	}
	
	public static void supprimer(Module m) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("delete from module where intitule = ?");
		ps.setString(1,m.getIntitule());
		ps.executeUpdate();
		ps.close();
	}
	
	public static void modifier(Module m1,Module m2,Promotion p) throws SQLException
	{
		Module.supprimer(m1);
		Module.ajouter(m2,p);
	}
	
	public static ObservableList<Module> consulter(Promotion p,String semestre) throws SQLException
	{
		ObservableList<Module> listModule = FXCollections.observableArrayList();
		PreparedStatement ps;
		if(semestre == null)
		{
			ps = connection.prepareStatement("select * from module where idPromotion = ?");
		}
		else
		{
			ps = connection.prepareStatement("select * from module where idPromotion = ? and Semestre = ?");
			ps.setString(2,semestre);
		}
		ps.setString(1,p.getIdentifiant());
		ResultSet result = ps.executeQuery();
		while(result.next())
		{
			listModule.add(new Module(result.getString("intitule"),result.getString("semestre")));
		}
		ps.close();
		ps = connection.prepareStatement("select * from assurer,enseignant where intitule = ? and assurer.nom "
				+ " = enseignant.nom and assurer.prenom = enseignant.prenom");
		Enseignant enseignant;
		for(Module m : listModule)
		{
			ps.setString(1,m.getIntitule());
			result = ps.executeQuery();
			while(result.next())
			{
				enseignant = new Enseignant();
				enseignant.setNom(result.getString("nom"));
				enseignant.setPrenom(result.getString("prenom"));
				enseignant.setGrade(result.getString("grade"));
				m.getListEnseignant().add(enseignant);
			}
		}
		return listModule;
	}
	//
	
	public String planningFormat()
	{
		String toRet = this.getIntitule()+"\n";
		if(listEnseignant.size() > 0)
		{
			toRet += "("+listEnseignant.get(0).getNom();
			for(int i=1;i<listEnseignant.size();i++)
			{
				toRet += " & "+listEnseignant.get(i).getNom();
			}
			toRet += ")";
		}
		return toRet;
	}
	public String texFormat()
	{
		String toRet;
		if(this.getIntitule() == null)
		{
			toRet = " \\\\ ";
		}
		else 
		{
			toRet = this.getIntitule()+" \\\\ ";
		}
		
		if(listEnseignant.size() > 0)
		{
			toRet += "(\\footnotesize "+listEnseignant.get(0).getNom();
			for(int i=1;i<listEnseignant.size();i++)
			{
				toRet += " \\& "+listEnseignant.get(i).getNom();
			}
			toRet += " \\normalsize )";
		}
		return toRet;
	}
	
	public Module asObject(String intitule) throws SQLException
	{
		Module module  = new Module();
		PreparedStatement ps = connection.prepareStatement("select * from module where intitule = ?");
		ps.setString(1,intitule);
		ResultSet result = ps.executeQuery();
		if(result.first())
		{
			module.setItitule(result.getString("intitule"));
			module.setSemestre(result.getString("idSemestre"));
			
			ps = connection.prepareStatement("select nom,prenom from assurer where intitule = ?");
			ps.setString(1,intitule);
			result = ps.executeQuery();
			
			ObservableList<Enseignant> listEnseignant = FXCollections.observableArrayList();

			while(result.next())
			{
				listEnseignant.add(Enseignant.asObejct(result.getString("nom"),result.getString("prenom")));
			}
			module.setListEnseignant(listEnseignant);
		}

		return module;
	}
	
	public String toString()
	{
		return this.getIntitule();
	}
	
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Module)) return false;
		Module m = (Module) o;
		if(this.getIntitule().equals(m.getIntitule())) return true;
		else return false;
	}

}
