package PFC.Model;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.nixosoft.jlr.JLRConverter;
import de.nixosoft.jlr.JLRGenerator;

public class Planning {
	
	private Promotion promotion;
	private String semestre;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	private ObservableList<Examen> listExamen = FXCollections.observableArrayList();
	private static Connection connection = BDDConnector.getInstance();
	
	private static BooleanProperty empty = new SimpleBooleanProperty();;

	public static void refrechEmptyStatut() throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery("select * from planning");
		if(result.first())
		{
			empty.set(false);
		}
		else 
		{
			empty.set(true);
		}
	}
	
	public Promotion getPromotion() {
		return promotion;
	}
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	public String getSemestre() {
		return semestre;
	}
	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}
	public LocalDate getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}
	public LocalDate getDateFin() {
		return dateFin;
	}
	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}
	
	public static BooleanProperty emptyProperty()
	{
		return empty;
	}
	
	public static boolean isEmpty()
	{
		return empty.getValue();
	}
	
	public ObservableList<Examen> getListExamen() {
		return listExamen;
	}
	public void setListExamen(ObservableList<Examen> listExamen) {
		this.listExamen = listExamen;
	}
	
	public static void ajouter(Planning planning) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into planning values(?,?,?,?)");
		ps.setString(1,planning.getPromotion().getIdentifiant());
		ps.setString(2,planning.getSemestre());
		ps.setDate(3,Date.valueOf(planning.getDateDebut()));
		ps.setDate(4,Date.valueOf(planning.getDateFin()));
		ps.executeUpdate();
		ps.close();
		
		for(Examen examen : planning.getListExamen())
		{
			Examen.ajouter(examen,planning);
		}
		
		if(empty.get() == true)
		{
			empty.set(false);
		}
	}
	
	public static ObservableList<Planning> consulter(Promotion promotion) throws SQLException
	{
		ObservableList<Planning> listPlanning = FXCollections.observableArrayList();
		PreparedStatement ps;
		if(promotion == null)
		{
			ps = connection.prepareStatement("select * from planning");
		}
		else 
		{
			ps = connection.prepareStatement("select * from planning where idPromotion = ?");
			ps.setString(1,promotion.getIdentifiant());
		}
		ResultSet result = ps.executeQuery();
		Planning planning;
		while(result.next())
		{
			planning = new Planning();
			planning.setPromotion(promotion.clone());
			planning.setSemestre(result.getString("semestre"));
		    Date d = result.getDate("date_debut");
			planning.setDateDebut(d.toLocalDate());
			d = result.getDate("date_fin");
			planning.setDateFin(d.toLocalDate());
			planning.setListExamen(Examen.consulter(planning));
			listPlanning.add(planning);
		}
		ps.close();
		return listPlanning;
	}
	
	public static void modifier(Planning planning1,Planning planning2) throws SQLException
	{
		Planning.supprimer(planning1);
		Planning.ajouter(planning2);
	}
	
	public static void supprimer(Planning planning) throws SQLException
	{
		for(Examen examen : planning.getListExamen())
		{
			Examen.supprimer(examen, planning);
		}
		
		PreparedStatement ps = connection.prepareStatement("delete from planning where idPromotion = ?");
        ps.setString(1,planning.getPromotion().getIdentifiant());
        ps.executeUpdate();
        ps.close();
        
        ObservableList<Planning> listPlanning = Planning.consulter(null);
        
        if(listPlanning.isEmpty())
        {
        	empty.set(true);
        }
	}
	
	public static File generateTexFile(Planning planning,String faculte,String dpt,String session) throws Exception
	{
		File workingDir = new File("TeX_Data");
		File template = new File(workingDir.getAbsolutePath()+File.separator+"template.tex");
		File texFile = new File(workingDir.getAbsolutePath()+File.separator+"planning.tex");
		JLRConverter convertor = new JLRConverter(new File("Tex_Data"));
		convertor.replace("faculte",faculte);
		convertor.replace("departement",dpt);
		convertor.replace("promotion",planning.getPromotion().getIdentifiant());
		convertor.replace("session", session);
		convertor.replace("nbgroupes",planning.getPromotion().getNbSec() * planning.getPromotion().getNbGroupes());
		convertor.replace("semestre",planning.getSemestre());
		convertor.replace("debut",planning.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		convertor.replace("fin",planning.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		convertor.replace("planning",planning);
		if(!convertor.parse(template,texFile))
		{
			throw new Exception(convertor.getErrorMessage());
		}
		return texFile;
	}
	
	public static File generatePDF() throws Exception
	{
		File workingDir = new File("TeX_Data");
		File texFile = new File(workingDir.getAbsolutePath()+File.separator+"planning.tex");
		JLRGenerator generator = new JLRGenerator();
		if(!generator.generate(texFile, workingDir, workingDir))
		{
			throw new Exception(generator.getErrorMessage());
		}
		return new File("Tex_Data\\planning.pdf");
	}
	
	public void debug()
	{
		System.out.println("Planning pour la promotion : "+this.getPromotion().getIdentifiant());
		System.out.println("Semestre : "+this.getSemestre());
		System.out.println("Periode des examens du "+this.getDateDebut()+" au "+this.getDateFin());
		System.out.println("list des examens : ");
		for(Examen examen : this.getListExamen())
		{
			System.out.println("Module:"+examen.getModule()+" Le "+examen.getDayName()+" "+examen.getDate()+" "+examen.getHeure());
			System.out.println("List des affectations: ");
			for(Surveiller survaillance : examen.getSurvaillances())
			{
				System.out.println("	Local:"+survaillance.getLocal());
				System.out.print("		List des groupes: ");
				for(String groupe : survaillance.getGroupes())
				{
					System.out.print(groupe+" ");
				}
				System.out.println();
				System.out.print("		List des survaillants: ");
				for(Enseignant e : survaillance.getSurveillants())
				{
					System.out.print(e.getNom()+" ");
				}
				System.out.println();
			}
		}
		System.out.println("-----------------------------------------------------------");
	}

}
