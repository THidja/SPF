package PFC.View.GP;



import java.net.URL;
import java.sql.SQLException;
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
import PFC.Model.Module;
import PFC.Model.Planning;
import PFC.Model.Promotion;

public class GPMainController implements Initializable, ControlledScreen {
	
	private Main main;
	private ScreensController myController;
	private ObservableList<Promotion> listPromotion;
	
	@FXML
	private BorderPane content;
	@FXML
	private TextField searchField;
	@FXML
	private TableView<Promotion> table;
	@FXML
	private TableColumn<Promotion,String> identifiantColumn;
	@FXML
	private TableColumn<Promotion,Number> nbSecColumn;
	@FXML
	private TableColumn<Promotion,Number> nbGroupesColumn;
	@FXML 
	private TableView<Module> modulesList;
	@FXML
	private TableColumn<Module,String> moduleColumn;
	@FXML 
	private Button addButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button editButton;
	@FXML 
	private Button gmButton;
	@FXML 
	private Button homeButton;
	@FXML
	private Button fsButton;

	@FXML
	private void addHandler()
	{
		content.toBack();
		main.showGPAddDialog(listPromotion);
		content.toFront();
	}
	
	@FXML
	private void editHandler()
	{
    	Promotion p = table.getSelectionModel().selectedItemProperty().get();
    	content.toBack();
        main.showGPEditDialog(listPromotion,p);
        content.toFront();
	}
    @FXML
    private void deleteHandler()
    {
    	Promotion promotion = table.getSelectionModel().selectedItemProperty().get();
    	Alert deleteConfirmDialog  = new Alert(AlertType.CONFIRMATION);
    	deleteConfirmDialog.initModality(Modality.APPLICATION_MODAL);
    	deleteConfirmDialog.initOwner(main.getPrimaryStage());
    	deleteConfirmDialog.setHeaderText("Voulez vous vraiment supprimer cette promotion ?");
    	deleteConfirmDialog.setContentText("Promotion: " + promotion.getIdentifiant());
        deleteConfirmDialog.getDialogPane().getStylesheets().add("alert.css");
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
    			Promotion.supprimer(promotion);
    			listPromotion.remove(promotion);
    			Planning.refrechEmptyStatut();
    			Notifications.create()
				  .title("Confirmation de la suppression")
				  .text("la promotion a bien etait supprimer")
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
	private void gmHandler()
	{
    	Promotion p = table.getSelectionModel().selectedItemProperty().get();
    	main.setActivePromotion(p);
    	content.toBack();
		main.createGMStage(p);
		content.toFront();
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
		
		identifiantColumn.setCellValueFactory(cellData -> cellData.getValue().identifiantProperty());
		nbSecColumn.setCellValueFactory(cellData -> cellData.getValue().nbSecProperty());
		nbGroupesColumn.setCellValueFactory(cellData -> cellData.getValue().nbGroupesProperty());
		
	    deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
	    editButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
	    gmButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		
	    moduleColumn.setCellValueFactory(cellData -> cellData.getValue().intituleProperty());
		
		table.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> {
			
			if(newValue != null)
			{
				modulesList.setItems(newValue.getModulesList());
			}
		});	
	}
	
	private void loadTableContent()
	{
		listPromotion = main.getListPromotion();
		
		FilteredList<Promotion> filteredList = new FilteredList<>(listPromotion, promotion -> true);
		searchField.textProperty().addListener((observable,oldValue,newValue) -> {
			
			filteredList.setPredicate(promotion -> {
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}
				if(promotion.getIdentifiant().toLowerCase().contains(newValue.toLowerCase()))
				{
					return true;
				}
				if(String.valueOf(promotion.getNbGroupes()).contains(newValue))
				{
					return true;
				}
				if(String.valueOf(promotion.getNbSec()).contains(newValue))
				{
					return true;
				}
				return false;
			});
		});
		SortedList<Promotion> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedList);
	}

	@Override
	public void viewed() {
		// TODO Auto-generated method stub
		
	}
}
