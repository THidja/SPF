package PFC.View.GP;

import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Module;
import PFC.Model.Promotion;

public class GMController {
	
	private Promotion promotion;
	private Stage gmStage; 
	private Alert errDialog;
	private Alert infoDialog;
	
	@FXML
	private TextField searchField;
	@FXML
	private TableView<Module> modulesList;
	@FXML 
	private TableColumn<Module,String> moduleColumn;
	@FXML
	private Button deleteButton;
	@FXML
	private Button editButton;
	@FXML 
	private TextField inputField;
	@FXML
	private ChoiceBox<String> semestreChoice;
	
	
	@FXML
	private void initialize()
	{
		semestreChoice.getItems().addAll("Semestre 1","Semestre 2","Semestre 3","Semestre 4","Semestre 5","Semestre 6");
		semestreChoice.getSelectionModel().select(0);
		
		deleteButton.disableProperty().bind(modulesList.getSelectionModel().selectedItemProperty().isNull());
		editButton.disableProperty().bind(modulesList.getSelectionModel().selectedItemProperty().isNull());
		
		moduleColumn.setCellValueFactory(cellData -> cellData.getValue().intituleProperty());
		
	    modulesList.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> {
			if(newValue != null)
			{
				inputField.setText(newValue.getIntitule());
				semestreChoice.setValue(newValue.getSemestre());
			}
		});
	    
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des modules - Erreur");
		errDialog.getDialogPane().getStylesheets().add("alert.css");

		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des modules - Information");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
	}
	@FXML 
	private void deleteHandler()
	{
        Module module = modulesList.getSelectionModel().getSelectedItem();
    	Alert deleteConfirmDialog  = new Alert(AlertType.CONFIRMATION);
    	deleteConfirmDialog.initModality(Modality.APPLICATION_MODAL);
    	deleteConfirmDialog.initOwner(gmStage);
    	deleteConfirmDialog.setHeaderText("Voulez vous vraiment supprimer ce module ?");
    	deleteConfirmDialog.setContentText("Module: " + module.getIntitule());
    	deleteConfirmDialog.getDialogPane().getStylesheets().add("alert.css");
        ButtonType buttonTypeOui = new ButtonType("Oui",ButtonData.YES);
        ButtonType buttonTypeNon = new ButtonType("Non",ButtonData.NO);
        deleteConfirmDialog.getButtonTypes().clear();
        deleteConfirmDialog.getButtonTypes().addAll(buttonTypeOui,buttonTypeNon);
    	Optional<ButtonType> result = deleteConfirmDialog.showAndWait();
    	if(result.get() == buttonTypeOui)
    	{
        	try {
        		Module.supprimer(module);
        		promotion.getModulesList().remove(module);
        	}
        	catch(SQLException e)
        	{
        		Main.showExceptionDialog(e);
        	}
    	}
	}
	@FXML
	private void ajouterHandler()
	{
		String inputFieldValue = inputField.getText();
		Module module = new Module(inputFieldValue,semestreChoice.getValue());
		if(!module.getIntitule().isEmpty())
		{
			boolean existInTheModulesList = false;
			for(Module m : promotion.getModulesList())
			{
				if(module.getIntitule().equals(m.getIntitule()))
				{
					existInTheModulesList = true;
				}
			}
			
			if(existInTheModulesList)
			{
				errDialog.setHeaderText("Erreur d'ajout");
				errDialog.setContentText("Le module existe déja");
				errDialog.showAndWait();
			}
			else
			{
				try {
					Module.ajouter(module, promotion);
					promotion.getModulesList().add(module);
					infoDialog.setHeaderText("Ajout effectué avec succés");
					infoDialog.setContentText("Le module "+inputFieldValue+" a bien été ajouté");
					infoDialog.showAndWait();
				}
				catch(SQLException e)
				{
					errDialog.setHeaderText("erreur d'ajout");
					errDialog.setContentText(e.getMessage());
					errDialog.showAndWait();
				}

			}
		}
		else 
		{
			errDialog.setHeaderText("Erreur d'ajout");
			errDialog.setContentText(" Veuillez saisir un intitulé correct!");
			errDialog.showAndWait();	
		}
	}
	@FXML
	private void editHandler()
	{
		String inputFieldValue = inputField.getText();
		Module module = modulesList.getSelectionModel().getSelectedItem();
		Module newModule = new Module(inputFieldValue,semestreChoice.getValue());
		
		if(!newModule.getIntitule().isEmpty())
		{
			boolean existInTheModulesList = false;
			for(Module m : promotion.getModulesList())
			{
				if(newModule.equals(m) && !m.equals(module))
				{
					existInTheModulesList = true;
				}
			}
			
			if(existInTheModulesList)
			{
				errDialog.setHeaderText("Erreur de modification");
				errDialog.setContentText("Le module éxiste déja");
				errDialog.showAndWait();
			}
			else
			{
    			
        		try {
        			Module.modifier(module,newModule,promotion);
        			module.setItitule(inputFieldValue);
        			module.setSemestre(semestreChoice.getValue());
        		}
        		catch(SQLException e)
        		{
        			errDialog.setHeaderText("Erreur de modification");
        			errDialog.showAndWait();
        		}
        		infoDialog.setHeaderText("Modification effectuée avec succés ");
        		infoDialog.setContentText("Le module a bien été modifié");
        		infoDialog.showAndWait();

			}
		}
		else 
		{
			errDialog.setHeaderText("Erreur de modification");
			errDialog.setContentText(" Veuillez saisir un intitulé correct!");
			errDialog.showAndWait();	
		}
	}
	public void setPromotion(Promotion promotion)
	{
		this.promotion = promotion;
		
	    searchField.textProperty().addListener((observable,oldValue,newValue) -> {
	    	FilteredList<Module> filteredList = new FilteredList<>(promotion.getModulesList(),m -> true);
	    	filteredList.setPredicate(module -> {
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
	    	modulesList.setItems(filteredList);
	    });
		modulesList.setItems(promotion.getModulesList());
	}
	
	public void setGMStage(Stage gmStage)
	{
		this.gmStage = gmStage;
		errDialog.initOwner(gmStage);
		infoDialog.initOwner(gmStage);
	}

}
