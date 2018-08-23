package PFC.View.GC;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import PFC.ControlledScreen;
import PFC.Main;
import PFC.ScreensController;
import PFC.Model.Creneau;
import PFC.Model.Local;

public class GCMainController implements Initializable, ControlledScreen {

	private Main main;
	private ScreensController myController;
	private ObservableList<Creneau> listCreneau;
	
	@FXML
	private BorderPane content;
	@FXML
	private TextField searchField;
	@FXML
	private TableView<Creneau> table;
	@FXML
	private TableColumn<Creneau,String> promotionColumn;
	@FXML
	private TableColumn<Creneau,LocalDate> dateColumn;
	@FXML
	private TableColumn<Creneau,LocalTime> heureColumn;
	
	
	@FXML 
	private TableView<Local> locauxTable;
	@FXML
	private TableColumn<Local,String> locauxColumn;

	
	@FXML 
	private Button addButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button editButton;
	@FXML
	private Button homeButton;
	@FXML
	private Button fsButton;
	
	@FXML
	private void addHandler()
	{
		content.toBack();
		main.showGCAddDialog(listCreneau);
		content.toFront();
	}
	
	@FXML
	private void editHandler()
	{
		Creneau creneau = table.getSelectionModel().selectedItemProperty().get();
		content.toBack();
		main.showGCEditDialog(listCreneau,creneau);
		content.toFront();
	}
	
    @FXML
	private void deleteHandler()
    {
    	Creneau creneau = table.getSelectionModel().selectedItemProperty().get();
    	Alert deleteConfirmDialog  = new Alert(AlertType.CONFIRMATION);
    	deleteConfirmDialog.getDialogPane().getStylesheets().add("alert.css");
    	deleteConfirmDialog.initOwner(main.getPrimaryStage());
    	deleteConfirmDialog.initModality(Modality.APPLICATION_MODAL);
    	deleteConfirmDialog.setHeaderText("Voulez vous vraiment supprimer ce créneau ?");
    	deleteConfirmDialog.setContentText("Créneau du '"+creneau.getDate()+" "+creneau.getheure()+"' de la promotion '"+
    											creneau.getPromotion()+"'");
        ButtonType buttonTypeOui = new ButtonType("Oui",ButtonData.YES);
        ButtonType buttonTypeNon = new ButtonType("Non",ButtonData.NO);
        deleteConfirmDialog.getButtonTypes().clear();
        deleteConfirmDialog.getButtonTypes().addAll(buttonTypeOui,buttonTypeNon);
        content.toBack();
    	Optional<ButtonType> result = deleteConfirmDialog.showAndWait();
    	content.toFront();
    	if(result.get() == buttonTypeOui)
    	{
    		try {
    			Creneau.supprimer(creneau);
    			listCreneau.remove(creneau);
    			Notifications.create()
    						  .title("Confirmation de la suppression")
    						  .text("le créneau a bien etait supprimer")
    						  .position(Pos.BOTTOM_RIGHT)
    						  .darkStyle()
    						  .show();
    		}
    		catch(SQLException e)
    		{
    			Main.showExceptionDialog(e);
        	}
    	}
    }
    
    @FXML 
    private void homeHandler()
    {
    	myController.setScreen(Main.mainScreen);
    }
    @FXML
    private void fsHandler()
    {
    	main.getPrimaryStage().setFullScreen(!main.getPrimaryStage().isFullScreen());
    }
    @FXML
    private void KeysHandler(KeyEvent event)
    {
    	if(event.getCode() == KeyCode.BACK_SPACE)
    	{
    		myController.setScreen(Main.mainScreen);
    	}
    	else if(event.isControlDown() && event.getCode() == KeyCode.N)
    	{
    		this.addHandler();
    	}
    	else if(event.isControlDown() && event.getCode() == KeyCode.D)
    	{
    		if(!deleteButton.isDisable())
    		{
    			this.deleteHandler();
    		}
    	}
    	else if(event.isControlDown() && event.getCode() == KeyCode.E)
    	{
    		if(!editButton.isDisable())
    		{
    			this.editHandler();
    		}
    	}
    }
	public void setMain(Main main)
	{
		this.main = main;
		loadTableContent();
	}
	
	@Override
	public void setScreenParent(ScreensController screenPage) {
		this.myController = screenPage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		addButton.setGraphic(new ImageView("Images/add.png"));
		deleteButton.setGraphic(new ImageView("Images/delete.png"));
		editButton.setGraphic(new ImageView("Images/edit.png"));
		homeButton.setGraphic(new ImageView("Images/back-icon.png"));
		fsButton.setGraphic(new ImageView("Images/fullscreen.png"));
		
		promotionColumn.setCellValueFactory(cellData -> cellData.getValue().promotionProperty().get().identifiantProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		heureColumn.setCellValueFactory(cellData -> cellData.getValue().heureProperty());
		
		table.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> {
			if(newValue != null)
			{
				locauxTable.setItems(table.getSelectionModel().getSelectedItem().getLocalList());
			}
		});
		
		locauxColumn.setCellValueFactory((cellData) -> cellData.getValue().identifiantProperty());
		
		deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		editButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
	}
	
	private void loadTableContent()
	{
		listCreneau = main.getListCreneau();
		
		FilteredList<Creneau> fileteredList = new FilteredList<>(listCreneau, creneau -> true);
		
		searchField.textProperty().addListener((observable,oldValue,newValue)-> {
			
			fileteredList.setPredicate( creneau -> {
				
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}
				if(creneau.getPromotion().getIdentifiant().toLowerCase().contains(newValue.toLowerCase()))
				{
					return true;
				}
				if(creneau.getDate().toString().contains(newValue.toLowerCase()))
				{
					return true;
				}
				if(creneau.getheure().toString().contains(newValue.toLowerCase()))
				{
					return true;
				}
				return false;
			});
		});
		SortedList<Creneau> items = new SortedList<>(fileteredList);
		items.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(items);
	}
	
	@Override
	public void viewed() {
		// TODO Auto-generated method stub
		
	}
}
