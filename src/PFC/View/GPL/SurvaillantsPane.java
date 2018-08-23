package PFC.View.GPL;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import PFC.Model.Enseignant;



public class SurvaillantsPane extends VBox {
	
	private ObservableList<Enseignant> data = FXCollections.observableArrayList();
	private LocalDate date;
	private LocalTime time;
	
	SurvaillantsPane(ObservableList<Enseignant> data)
	{
		this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		this.setAlignment(Pos.CENTER_LEFT);
		this.setData(data);
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public ObservableList<Enseignant> getData() {
		return data;
	}

	public void setData(ObservableList<Enseignant> data) {
		this.data = data;
	}
	
}
