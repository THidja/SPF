package PFC.View.GPL;



import org.controlsfx.control.CheckListView;

import PFC.Util.DataOperations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GPLGroupeChooser extends Stage {
	
	private BorderPane layoutPane;
	private CheckListView<String> checkList;
	private ObservableList<String> state; // Saved State

	public GPLGroupeChooser()
	{
		this.setTitle("Affecter des groupes");
		Scene content; 
		layoutPane = new BorderPane();
		layoutPane.setPrefSize(300,400);
		// Content Data 
    	checkList = new CheckListView<>(FXCollections.observableArrayList());
    	checkList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		
		layoutPane.setCenter(checkList);
		
		// ButtonsBar Data 
			
		HBox hbox = new HBox(8);
			
		// Button Annuler
		Button annuler = new Button("Annuler");
		annuler.setOnAction((event) ->{checkList.getCheckModel().clearChecks();this.checkElements(state); this.close(); });
		
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
		layoutPane.setBottom(hbox);
			
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
	
	public void checkElements(ObservableList<String> list)
	{
		if(list == null)
	    {
		  checkList.getCheckModel().clearChecks();
	    }
		else
		for(String g : list)
		{
			checkList.getCheckModel().check(g);
		}
	}
	
	public ObservableList<String> getElements()
	{
		return this.checkList.getItems();
	}
	
	public String getData()
	{
		return DataOperations.groupesOf(getCheckedElements());
	}
	
	public ObservableList<String> getCheckedElements()
	{
		return checkList.getCheckModel().getCheckedItems();
	}

	public ObservableList<String> getGroupes() {
		return checkList.getItems();
	}

	public void setGroupes(ObservableList<String> groupes)
	{
		FXCollections.sort(groupes);
		this.checkList = new CheckListView<String>(FXCollections.observableArrayList(groupes));
		layoutPane.setCenter(checkList);
	}
	public void addGroupes(ObservableList<String> groupes)
	{
		this.getGroupes().addAll(groupes);
		FXCollections.sort(this.getGroupes());
		this.checkList = new CheckListView<String>(FXCollections.observableArrayList(this.getGroupes()));
		layoutPane.setCenter(checkList);
	}
	public void initParent(Stage stage)
	{
		this.initModality(Modality.APPLICATION_MODAL);
		this.initOwner(stage);
		this.setOnShown(event -> {
		 	this.setX(stage.getX() + stage.getWidth() / 2 - this.getWidth() / 2);
		 	this.setY(stage.getY() + stage.getHeight() / 2 - this.getHeight() / 2);
		});
	}
}
