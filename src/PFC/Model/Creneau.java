package PFC.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class Creneau {
	
	private ObjectProperty<Promotion> promotion;
	private ObjectProperty<LocalDate> date;
	private ObjectProperty<LocalTime> heure;
	private ObservableList<Local> localList = FXCollections.observableArrayList();
	private static Connection connection = BDDConnector.getInstance();
	
	public Creneau(Promotion promotion,LocalDate date,LocalTime heure) 
	{
		this.promotion = new SimpleObjectProperty<Promotion>(promotion);
		this.date = new SimpleObjectProperty<LocalDate>(date);
		this.heure = new SimpleObjectProperty<LocalTime>(heure);
	}
	
	public Creneau()
	{
		this.promotion = new SimpleObjectProperty<Promotion>();
		this.date = new SimpleObjectProperty<LocalDate>();
		this.heure = new SimpleObjectProperty<LocalTime>();
	}
	
	// DataBase Operations 
	
	public static void ajouter(Creneau c) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("insert into creneau values(?,?,?,?)");
		ps.setDate(1,Date.valueOf(c.getDate()));
		ps.setTime(2,Time.valueOf(c.getheure()));
		ps.setString(4,c.getPromotion().getIdentifiant());
		for(Local l : c.getLocalList())
		{
			ps.setString(3,l.getIdentifiant());
			ps.executeUpdate();
		}
		ps.close();
	}
	
	public static ObservableList<Creneau> consulter(Promotion p,LocalDate d1,LocalDate d2) throws SQLException
	{
		ObservableList<Creneau> listCreneau = FXCollections.observableArrayList();
		PreparedStatement psGetPromotion;
		PreparedStatement psGetIdLocal;
		PreparedStatement psGetLocal;
		ResultSet result;
		if(p != null)
		{
			if(d1 != null && d2 != null)
			{
				PreparedStatement ps = connection.prepareStatement("select distinct date,heure,idPromotion from creneau "
						+ " where idPromotion = ? and date between ? and ?");
				ps.setString(1,p.getIdentifiant());
				ps.setDate(2,Date.valueOf(d1));
				ps.setDate(3,Date.valueOf(d2));
				result = ps.executeQuery();
			}
			else
			{
				PreparedStatement ps = connection.prepareStatement("select distinct date,heure,idPromotion from creneau "
						+ " where idPromotion =  ?");
				ps.setString(1,p.getIdentifiant());
				result = ps.executeQuery();
			}
		}
		else 
		{
			if(d1 != null && d2 != null)
			{
				PreparedStatement ps = connection.prepareStatement("select distinct date,heure,idPromotion from creneau "
						+ "where date between ? and ?");
				ps.setDate(1,Date.valueOf(d1));
				ps.setDate(2,Date.valueOf(d2));
				result = ps.executeQuery();
			}
			else 
			{
				Statement state = connection.createStatement();
				result = state.executeQuery("select distinct date,heure,idPromotion from creneau");
			}
		}
		
		ResultSet promotionResult;
		ResultSet idLocalResult;
		ResultSet localResult;
		Creneau c;
		while(result.next())
		{
			c = new Creneau();
			c.setDate(result.getDate("date").toLocalDate());
			c.setheure(result.getTime("heure").toLocalTime());
			psGetPromotion = connection.prepareStatement("select * from promotion where idPromotion = ?");
			psGetPromotion.setString(1,result.getString("idPromotion"));
			promotionResult = psGetPromotion.executeQuery();
			promotionResult.next();
			Promotion promotion = new Promotion();
			promotion.setIdentifiant(promotionResult.getString("idPromotion"));
			promotion.setNbSec(promotionResult.getInt("nbSections"));
			promotion.setNbGroupes(promotionResult.getInt("nbGroupes"));
			c.setPromotion(promotion);
			psGetIdLocal = connection.prepareStatement("select idLocal from creneau where date = ? and heure = ? and idPromotion = ?");
			psGetIdLocal.setDate(1,result.getDate("date"));
			psGetIdLocal.setTime(2,result.getTime("heure"));
			psGetIdLocal.setString(3,promotion.getIdentifiant());
			idLocalResult = psGetIdLocal.executeQuery();
			while(idLocalResult.next())
			{
				psGetLocal = connection.prepareStatement("select * from local where idLocal = ?");
				psGetLocal.setString(1,idLocalResult.getString("idLocal"));
				localResult = psGetLocal.executeQuery();
				localResult.next();
				Local l = new Local();
				l.setIdentifiant(localResult.getString("idLocal"));
				l.setCapacite(localResult.getInt("capacite"));
				c.getLocalList().add(l);
			}
			listCreneau.add(c);
		}
		return listCreneau;
	}
	
	public static void supprimer(Creneau c) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement("delete from creneau where date = ? and heure = ? "
																								+ "and idPromotion = ?");
		ps.setDate(1,Date.valueOf(c.getDate()));
		ps.setTime(2,Time.valueOf(c.getheure()));
		ps.setString(3,c.getPromotion().getIdentifiant());
		ps.executeUpdate();
		ps.close();
	}
	
	public static void modifier(Creneau c1,Creneau c2) throws SQLException
	{
		Creneau.supprimer(c1);
		Creneau.ajouter(c2);
	}
	
	// date property manipulations
	
	public LocalDate getDate()
	{
		return date.get();
	}
	
	public void setDate(LocalDate date) 
	{
		this.date.set(date);
	}
	
	public ObjectProperty<LocalDate> dateProperty() 
	{
		return date;
	}
	
	// hour property manipulations
	
	public LocalTime getheure() 
	{
		return heure.get();
	}
	
	public void setheure(LocalTime heure) 
	{
		this.heure.set(heure);
	}
	
	public ObjectProperty<LocalTime> heureProperty()
	{
		return heure;
	}
	
	// promotion property manipulations 
	
	public ObjectProperty<Promotion> promotionProperty() 
	{
		return promotion;
	}

	public void setPromotion(Promotion promotion)
	{
		this.promotion.set(promotion);
	}
	
	public Promotion getPromotion()
	{
		return promotion.get();
	}
	
	// localList operations 
	
	public ObservableList<Local> getLocalList()
	{
		return localList;
	}
	
	public void setLocalList(ObservableList<Local> localList)
	{
		this.localList = localList;
	}
	
	public String toString()
	{
		String toRet = "";
		for(Local l : this.localList)
		{
			toRet += this.getPromotion().getIdentifiant()+" "+this.getDate()+" "+this.getheure()+" "+l+" \n";
		}
		return toRet;
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof Creneau)) return false;
		Creneau c = (Creneau) o;
		if(c.getDate().equals(this.getDate()) && c.getheure().equals(this.getheure()))
		{
			for(Local l : c.getLocalList())
			{
				if(this.getLocalList().contains(l))
				{
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
}
