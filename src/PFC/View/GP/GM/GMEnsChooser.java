package PFC.View.GP.GM;



import java.sql.SQLException;

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

import PFC.Main;
import PFC.Model.Enseignant;

public class GMEnsChooser extends Stage {
	
	private BorderPane layoutPane;
	private CheckListView<Enseignant> checkList;
	private ObservableList<Enseignant> state; // Saved State
	TextField searchField = new TextField();

	public GMEnsChooser()
	{
		Scene content; 
		layoutPane = new BorderPane();
		layoutPane.setPrefSize(300,400);
		// Content Data 
		try {
			checkList = new CheckListView<>(Enseignant.consulter());
	    	checkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		
			layoutPane.setCenter(checkList);
			
			// ButtonsBar Data 
				
			HBox hbox = new HBox(8);
			HBox hsbox = new HBox(8);
			VBox searchbox = new VBox();
			searchbox.setMaxWidth(300);
				
			// Button Annuler
			Button annuler = new Button("Annuler");
			annuler.setOnAction((event) ->{ checkList.getCheckModel().clearChecks(); this.checkElements(state); this.close(); });
			annuler.setPrefSize(67,27);
			annuler.setCancelButton(true);
			// Button Valider
			Button valider = new Button("Valider");
			valider.setOnAction((event) -> {this.getCheckedElements(); this.close(); });
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
		catch(SQLException e)
		{
			Main.showExceptionDialog(e);
		}
	}
	
	public void showAndWait()
	{
		state = FXCollections.observableArrayList(this.getCheckedElements());
		super.showAndWait();
	}
	
	public void checkElements(ObservableList<Enseignant> list)
	{
		if(list == null)
		{
			checkList.getCheckModel().clearChecks();
		}else
		for(Enseignant e : list)
		{
			checkList.getCheckModel().check(e);
		}
	}
	
	
	public ObservableList<Enseignant> getCheckedElements()
	{
		return checkList.getCheckModel().getCheckedItems();
	}

	public ObservableList<Enseignant> getListEnseignant() {
		return checkList.getItems();
	}

	public void setListEnseignant(ObservableList<Enseignant> listEnseignant) {
		
		this.checkList = new CheckListView<Enseignant>(FXCollections.observableArrayList(listEnseignant));
		layoutPane.setCenter(checkList);
	}
	
	public void setListContent(ObservableList<Enseignant> listEnseignants,ObservableList<Enseignant> checkedEnseignants)
	{
		FilteredList<Enseignant> fileteredList = new FilteredList<>(listEnseignants, enseignant -> true);
		
		searchField.textProperty().addListener((observable,oldValue,newValue)-> {
			if (checkedEnseignants != null)
			{
			checkedEnseignants.clear();	
			checkedEnseignants.setAll(this.getCheckedElements());
			this.checkElements(null);
			}
			fileteredList.setPredicate( enseignant -> {
				
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}
				if(enseignant.getNom().toLowerCase().contains(newValue.toLowerCase()) || enseignant.getPrenom().toLowerCase().contains(newValue.toLowerCase()))
				{
					return true;
				}
				if(checkedEnseignants.contains(enseignant))
				{
					return true;
				}	
				return false;
			});
			this.checkElements(checkedEnseignants);
		});
		SortedList<Enseignant> items = new SortedList<>(fileteredList);
		checkList.setItems(items);
		if (checkedEnseignants != null)
		this.checkElements(checkedEnseignants);

	}

}
