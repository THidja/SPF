package PFC.View.GC;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import org.controlsfx.control.CheckListView;

import PFC.Model.BDDConnector;
import PFC.Model.Local;

public class GCLocauxChooser extends Stage {
	
	private BorderPane layoutPane;
	private CheckListView<Local> checkList;
	private ObservableList<Local> state; // Saved State
	private LocalTime heure;
	private LocalDate date;
	TextField searchField = new TextField();
	

	public GCLocauxChooser()
	{
		init();
	}
	
	private void init()
	{
		Scene content; 
		layoutPane = new BorderPane();
		layoutPane.setPrefSize(300,400);
		
		this.checkList = new CheckListView<Local>(FXCollections.observableArrayList());
		
		// ButtonsBar Data 
			
		HBox hbox = new HBox(8);
		HBox hsbox = new HBox(8);
		VBox searchbox = new VBox();
		searchbox.setMaxWidth(300);
		
		// Button Annuler
		Button annuler = new Button("Annuler");
		annuler.setOnAction((event) ->{ this.checkElements(null);; this.checkElements(state); this.close(); });
		annuler.setPrefSize(67,27);
		annuler.setCancelButton(true);
		// Button Valider
		Button valider = new Button("Valider");
		valider.setOnAction((event) -> { this.close(); });
		valider.setPrefSize(67,27);
		valider.setDefaultButton(true);
		// Add Buttons to the layout-pane(HBox)
		hbox.getChildren().add(annuler);
		hbox.getChildren().add(valider);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.setPadding(new Insets(5));
		//searchField
		Label searchLabel = new Label("Rechercher");
		searchLabel.setTextAlignment(TextAlignment.CENTER);
		searchLabel.setFont(Font.font("Cambria", FontWeight.BOLD, 15));
		searchLabel.setStyle("-fx-text-fill:rgb(255,255,255);");
		
		searchbox.getChildren().add(searchLabel);
		searchbox.getChildren().add(searchField);
		searchbox.setAlignment(Pos.CENTER);
		searchbox.setPadding(new Insets(5));
		searchbox.setSpacing(5);
		hsbox.setAlignment(Pos.CENTER);
		hsbox.getChildren().add(searchbox);
		
		layoutPane.setStyle("-fx-background-color:rgb(46,55,61);");
		layoutPane.setBottom(hbox);
		layoutPane.setTop(hsbox);
			
		content = new Scene(layoutPane);
		content.getStylesheets().add("/PFC/View/design.css");
		this.setResizable(false);
		super.setScene(content);
	}
	
	public void showAndWait()
	{
		state = FXCollections.observableArrayList(this.getCheckedElements());
		super.showAndWait();
	}
	
	public void checkElements(ObservableList<Local> list)
	{	
		if(list == null)
		{
			checkList.getCheckModel().clearChecks();
		}else
		for(Local l : list)
		{
			checkList.getCheckModel().check(l);
		}
	}
	
	public ObservableList<Local> getCheckedElements()
	{
		return checkList.getCheckModel().getCheckedItems();
	}
	
    public void refrechLocaux()
    {
       ObservableList<Local> newData = FXCollections.observableArrayList();
    	try {
    		Connection connection = BDDConnector.getInstance();
    		PreparedStatement ps  = connection.prepareStatement("select * from local where idLocal"
    				+ " not in (select idLocal from creneau where ? between heure and ADDTIME(heure,'01:59') and date = ?)");
    		ps.setTime(1,Time.valueOf(getHeure()));
    		ps.setDate(2,Date.valueOf(getDate()));
    		ResultSet result = ps.executeQuery();
    		Local l;
    		while(result.next())
    		{
    			l = new Local();
    			l.setIdentifiant(result.getString("idLocal"));
    			l.setCapacite(result.getInt("capacite"));
    			newData.add(l);
    		}
    		
    		checkList = new CheckListView<Local>(newData);
    		checkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    		layoutPane.setCenter(checkList);
    		
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}

    }
	public LocalTime getHeure() {
		return heure;
	}

	public void setHeure(LocalTime heure) {
		this.heure = heure;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public ObservableList<Local> getItems() {
		return this.checkList.getItems();
	}
	
	public void addItems(ObservableList<Local> items)
	{
		this.checkList.getItems().addAll(items);
		this.checkList = new CheckListView<Local>(checkList.getItems());
		checkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		layoutPane.setCenter(checkList);
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public void setListContent(ObservableList<Local> listLocaux,ObservableList<Local> checkedLocaux)
	{
		FilteredList<Local> fileteredList = new FilteredList<>(listLocaux, local -> true);
		
		searchField.textProperty().addListener((observable,oldValue,newValue)-> {
			if (checkedLocaux != null)
			{	
			checkedLocaux.clear();	
			checkedLocaux.setAll(this.getCheckedElements());
			this.checkElements(null);
			}
			fileteredList.setPredicate( local -> {
				
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}
				if(local.getIdentifiant().toLowerCase().contains(newValue.toLowerCase()))
				{
					return true;
				}
				if(checkedLocaux.contains(local))
				{
					return true;
				}	
				return false;
			});
			
			this.checkElements(checkedLocaux);
		});
		SortedList<Local> items = new SortedList<>(fileteredList);
		checkList.setItems(items);
		if (checkedLocaux != null)
		this.checkElements(checkedLocaux);
	}
	
}
