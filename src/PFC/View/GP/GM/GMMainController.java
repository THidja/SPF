package PFC.View.GP.GM;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

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

import org.controlsfx.control.Notifications;

import PFC.ControlledScreen;
import PFC.Main;
import PFC.ScreensController;
import PFC.Model.Enseignant;
import PFC.Model.Module;
import PFC.Model.Promotion;

public class GMMainController implements Initializable, ControlledScreen {

	private Main main;
	private Promotion promotion;
	private ScreensController myController;
	
	@FXML
	private BorderPane content;
	@FXML
	private TextField searchField;
	@FXML
	private TableView<Module> table;
	@FXML
	private TableColumn<Module,String> moduleColumn;

	@FXML 
	private TableView<Enseignant> ensTable;
	@FXML
	private TableColumn<Enseignant,String> nomColumn;
	@FXML
	private TableColumn<Enseignant,String>  prenomColumn;
	
	@FXML 
	private Button addButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button editButton;
	@FXML
	private Button precButton;
	@FXML
	private Button fsButton;

    
	@FXML
	private void addHandler()
	{
		content.toBack();
		main.showGMAddDialog();
		content.toFront();
	}
	
	@FXML
	private void editHandler()
	{
		Module module = table.getSelectionModel().selectedItemProperty().get();
		content.toBack();
		main.showGMEditDialog(module);
		content.toFront();
	}
	
    @FXML
	private void deleteHandler()
    {
    	Module module = table.getSelectionModel().selectedItemProperty().get();
    	Alert deleteConfirmDialog  = new Alert(AlertType.CONFIRMATION);
    	deleteConfirmDialog.getDialogPane().getStylesheets().add("alert.css");
    	deleteConfirmDialog.initOwner(main.getPrimaryStage());
    	deleteConfirmDialog.initModality(Modality.APPLICATION_MODAL);
    	deleteConfirmDialog.setHeaderText("Voulez vous vraiment supprimer ce module ?");
    	deleteConfirmDialog.setContentText("Module du "+module.getIntitule());
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
    			Module.supprimer(module);
    			promotion.getModulesList().remove(module);
    			Notifications.create()
				  .title("Confirmation de la suppression")
				  .text("le module a bien etait supprimer")
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
    private void precHandler()
    {
    	myController.setScreen(Main.gpScreen);
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
    		myController.setScreen(Main.gpScreen);
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
	
	private void loadTableContent()
	{
		this.promotion = main.getActivePromotion();
		
		FilteredList<Module> fileteredList = new FilteredList<>(promotion.getModulesList(), module -> true);
		searchField.textProperty().addListener((observable,oldValue,newValue)-> {
		fileteredList.setPredicate( module -> {
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}
				if(module.getIntitule().toLowerCase().contains(searchField.getText().toLowerCase()))
				{
					return true;
				}
				return false;
			});
		});
		SortedList<Module> sortedList = new SortedList<>(fileteredList);
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedList);
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
		precButton.setGraphic(new ImageView("Images/back-icon.png"));
		fsButton.setGraphic(new ImageView("Images/fullscreen.png"));
		
		moduleColumn.setCellValueFactory(cellData -> cellData.getValue().intituleProperty());
	
		table.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> {
		if(newValue != null)
		{
			ensTable.setItems(table.getSelectionModel().getSelectedItem().getListEnseignant());
		}
		else
		{
			//ensTable.getItems().clear();
		}
		});
		
		nomColumn.setCellValueFactory((cellData) -> cellData.getValue().NomProperty());
		prenomColumn.setCellValueFactory((cellData) -> cellData.getValue().PrenomProperty());
		
		deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		editButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
	}

	@Override
	public void viewed() {
		// TODO Auto-generated method stub
		
	}

}
