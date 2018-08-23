package PFC.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import PFC.Util.DateOperations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Examen {
	
	private LocalDate date;
	private LocalTime heure;
	private Module module;
	private ObservableList<Surveiller> survaillances;
	private static Connection connection = BDDConnector.getInstance();
	
	public Examen()
	{
		date = null;
		heure = null;
		module = new Module();
		survaillances = FXCollections.observableArrayList();
	}
	
	
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	
	public LocalDate getDate() {
		return date;
	}
	public String getDateForPlanning()
	{
		return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getHeure() {
		return heure;
	}
	public void setHeure(LocalTime heure) {
		this.heure = heure;
	}
	public ObservableList<Surveiller> getSurvaillances() {
		return survaillances;
	}
	public void setSurvaillances(ObservableList<Surveiller> survaillances) {
		this.survaillances = survaillances;
	}
	
	public String getDayName()
	{
		return DateOperations.getDayOf(this.getDate().getDayOfWeek().getValue());
	}
	
	public static void ajouter(Examen examen,Planning planning) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into examen values(?,?,?,?)");
		ps.setString(1,examen.getModule().getIntitule());
		ps.setDate(2,Date.valueOf(examen.getDate()));
		ps.setTime(3,Time.valueOf(examen.getHeure()));
		ps.setString(4,planning.getPromotion().getIdentifiant());
		ps.executeUpdate();
		ps.close();
		
		ps = connection.prepareStatement("insert into surveiller values(?,?,?,?,?,?)");
		ps.setDate(3,Date.valueOf(examen.getDate()));
		ps.setTime(4,Time.valueOf(examen.getHeure()));
		ps.setString(5,planning.getPromotion().getIdentifiant());
		for(Surveiller survaillance : examen.getSurvaillances())
		{
			ps.setString(6,survaillance.getLocal().getIdentifiant());
			for(Enseignant enseignant : survaillance.getSurveillants())
			{
				ps.setString(1,enseignant.getNom());
				ps.setString(2,enseignant.getPrenom());
				ps.executeUpdate();
			}
		}
		ps = connection.prepareStatement("insert into affecter values(?,?,?,?,?)");
		ps.setDate(2,Date.valueOf(examen.getDate()));
		ps.setTime(3,Time.valueOf(examen.getHeure()));
		ps.setString(4,planning.getPromotion().getIdentifiant());
		for(Surveiller survaillance : examen.getSurvaillances())
		{
			ps.setString(5,survaillance.getLocal().getIdentifiant());
			for(String groupe : survaillance.getGroupes())
			{
				ps.setString(1,groupe);
				ps.executeUpdate();
			}
		}
	}
	
	public static ObservableList<Examen> consulter(Planning planning) 
	{
		ObservableList<Examen> listExamen = FXCollections.observableArrayList();
		try {
		
		PreparedStatement ps = connection.prepareStatement("select * from examen where idPromotion = ?");
		ps.setString(1,planning.getPromotion().getIdentifiant());
		ResultSet result = ps.executeQuery();
		Examen examen;
		while(result.next())
		{
			examen = new Examen();
			
			if(result.getString("intitule") == null)
			{
				Module m = new Module();
				examen.setModule(m);
			}
			else 
			{
				PreparedStatement getModule = connection.prepareStatement("select * from Module where intitule = ?");
				getModule.setString(1,result.getString("intitule"));
				ResultSet getModuleResult = getModule.executeQuery();
				if(getModuleResult.first())
				{
					Module module = new Module();
					module.setItitule(getModuleResult.getString("intitule"));
					module.setSemestre(getModuleResult.getString("semestre"));
					ObservableList<Enseignant> listEnseignant = FXCollections.observableArrayList();
					PreparedStatement getCharges = connection.prepareStatement("select * from enseignant,assurer where "
							+ "assurer.intitule = ? and Enseignant.nom = assurer.nom and Enseignant.prenom = assurer.prenom");
					getCharges.setString(1,module.getIntitule());
					ResultSet getChargesResult = getCharges.executeQuery();
					Enseignant enseignant;
					while(getChargesResult.next())
					{
						enseignant = new Enseignant();
						enseignant.setNom(getChargesResult.getString("nom"));
						enseignant.setPrenom(getChargesResult.getString("prenom"));
						enseignant.setGrade(getChargesResult.getString("grade"));
						listEnseignant.add(enseignant);
					}
					module.setListEnseignant(listEnseignant);
					examen.setModule(module);
			     }
			}
				Date d = result.getDate("date");
				examen.setDate(d.toLocalDate());
				Time t = result.getTime("heure");
				examen.setHeure(t.toLocalTime());
				ObservableList<Surveiller> survaillances = FXCollections.observableArrayList();
				Surveiller survaillance;
				
				PreparedStatement getLocaux = connection.prepareStatement("select distinct creneau.idLocal as idLocal,"
						+ "local.capacite as capacite from local,creneau where date = ? and heure = ? and idPromotion = ?"
						+ " and local.idLocal = creneau.idLocal");
				getLocaux.setDate(1,Date.valueOf(examen.getDate()));
				getLocaux.setTime(2,Time.valueOf(examen.getHeure()));
				getLocaux.setString(3,planning.getPromotion().getIdentifiant());
				
				ResultSet getLocauxResult = getLocaux.executeQuery();
				Local l;
				while(getLocauxResult.next())
				{
					ObservableList<String> listGroupe = FXCollections.observableArrayList();
					ObservableList<Enseignant> listSurvaillant = FXCollections.observableArrayList();
					survaillance = new Surveiller();
					l = new Local();
					l.setIdentifiant(getLocauxResult.getString("idLocal"));
					l.setCapacite(getLocauxResult.getInt("capacite"));
					survaillance.setLocal(l);
					PreparedStatement getGroupes = connection.prepareStatement("select * from affecter where date = ? and "
							+ "heure = ? and idPromotion = ? and idLocal = ?");
					getGroupes.setDate(1,Date.valueOf(examen.getDate()));
					getGroupes.setTime(2,Time.valueOf(examen.getHeure()));
					getGroupes.setString(3,planning.getPromotion().getIdentifiant());
					getGroupes.setString(4,l.getIdentifiant());
					ResultSet getGroupesResult = getGroupes.executeQuery();
					String groupe;
					while(getGroupesResult.next())
					{
						groupe = getGroupesResult.getString("groupe");
						listGroupe.add(groupe);
					}
					survaillance.setGroupes(listGroupe);
			    	PreparedStatement getSurvaillants = connection.prepareStatement("select * from enseignant,surveiller where "
						+ "date = ? and heure = ? and idPromotion = ? and enseignant.nom = surveiller.nom "
						+ "and enseignant.prenom = surveiller.prenom and idLocal = ?");
			    	getSurvaillants.setDate(1,Date.valueOf(examen.getDate()));
				    getSurvaillants.setTime(2,Time.valueOf(examen.getHeure()));
				    getSurvaillants.setString(3,planning.getPromotion().getIdentifiant());
				    getSurvaillants.setString(4,l.getIdentifiant());
				    ResultSet getSurvaillantsResult = getSurvaillants.executeQuery();
				    Enseignant survaillant;
				    while(getSurvaillantsResult.next())
				    {
				    	survaillant = new Enseignant();
				    	survaillant.setNom(getSurvaillantsResult.getString("nom"));
				    	survaillant.setPrenom(getSurvaillantsResult.getString("prenom"));
				    	survaillant.setGrade(getSurvaillantsResult.getString("grade"));
				    	listSurvaillant.add(survaillant);
				    }
				    survaillance.setSurveillants(listSurvaillant);
				    survaillances.add(survaillance);
				}
				examen.setSurvaillances(survaillances);
				listExamen.add(examen);
			} 
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return listExamen;
	}
	
	public static void supprimer(Examen examen,Planning planning) throws SQLException
	{
		PreparedStatement srvDelete = connection.prepareStatement("delete from surveiller where idPromotion = ? and "
				+ "date = ? and heure = ? ");
		srvDelete.setString(1,planning.getPromotion().getIdentifiant());
		srvDelete.setDate(2,Date.valueOf(examen.getDate()));
		srvDelete.setTime(3,Time.valueOf(examen.getHeure()));
		srvDelete.executeUpdate();
		PreparedStatement afctDelete = connection.prepareStatement("delete from affecter where idPromotion = ? and "
				+ "date = ? and heure = ?");
		afctDelete.setString(1,planning.getPromotion().getIdentifiant());
		afctDelete.setDate(2,Date.valueOf(examen.getDate()));
		afctDelete.setTime(3,Time.valueOf(examen.getHeure()));
		afctDelete.executeUpdate();
		PreparedStatement exmDelete = connection.prepareStatement("delete from examen where idPromotion = ? and "
				+ "date = ? and heure = ?");
		exmDelete.setString(1,planning.getPromotion().getIdentifiant());
		exmDelete.setDate(2,Date.valueOf(examen.getDate()));
		exmDelete.setTime(3,Time.valueOf(examen.getHeure()));
		exmDelete.executeUpdate();
	}
	
}
