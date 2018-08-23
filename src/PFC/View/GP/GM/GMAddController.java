package PFC.View.GP.GM;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Enseignant;
import PFC.Model.Module;
import PFC.Model.Promotion;
import PFC.Util.DataOperations;


public class GMAddController {
	
	private Stage addStage;
	private Alert errDialog;
	private Alert infoDialog;
	private ObservableList<Enseignant> listEnseignant = FXCollections.observableArrayList();
	
	private Promotion promotion;
	
	@FXML
	private TextField intituleField;
	@FXML
	private ChoiceBox<String> semestreChoice;

	
	@FXML
	private void initialize()
	{
		semestreChoice.getItems().addAll("Semestre 1","Semestre 2","Semestre 3","Semestre 4","Semestre 5","Semestre 6");
		semestreChoice.getSelectionModel().select(0);
		
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des Modules - Erreur");
		errDialog.setHeaderText("Erreur d'ajout");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des Modules - Information");
		infoDialog.setHeaderText("L'ajout a etait effectué avec succes");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
		
	}
    @FXML 
	private void ecButtonHandler()
	{
    	GMEnsChooser ec = new GMEnsChooser();
		ec.initModality(Modality.APPLICATION_MODAL);
		ec.initOwner(addStage);
		
		ec.setListContent(ec.getListEnseignant(),listEnseignant);
    	ec.showAndWait();
    	listEnseignant.setAll(ec.getCheckedElements());
		
	 }
    @FXML
    private void validerHandler()
	{
        
    	boolean valide = true;
    	String errMsg = "";
    	
		String inputFieldValue = intituleField.getText();
		
		if(inputFieldValue.isEmpty())
		{
			valide = false;
			errMsg += "L’intitulé du module n’est pas spécifié! \n";
		}
		else 
		{
			inputFieldValue = DataOperations.getValideName(inputFieldValue);
			inputFieldValue = DataOperations.capitalize(inputFieldValue);
		}
		if(listEnseignant.size() == 0)
		{
			valide = false;
			errMsg += "Attention,au moins un chargé du module doit étre défini! \n";
		}
		
		if(valide)
		{	
			
			Module module = new Module();
			module.setItitule(inputFieldValue);
			module.setSemestre(semestreChoice.getValue());
			module.setListEnseignant(listEnseignant);
			
			boolean existInTheModulesList = false;
			
			for(Module m : promotion.getModulesList())
			{
				if(module.equals(m))
				{
					existInTheModulesList = true;
				}
			}
			if(existInTheModulesList)
			{
				errDialog.setHeaderText("Erreur d'ajout");
				errDialog.setContentText("Le module éxiste déja");
				errDialog.showAndWait();
			}
			else
			{
				try {
					Module.ajouter(module, promotion);
					promotion.getModulesList().add(module);
					infoDialog.setHeaderText("Ajout effectué avec succés");
					infoDialog.setContentText("Le module '"+inputFieldValue+"' a bien été ajouté");
					infoDialog.showAndWait();
					addStage.close();
				}
				catch(SQLException e)
				{
					Main.showExceptionDialog(e);
				}
			}
		}
		else 
		{
			errDialog.setHeaderText("Erreur d'ajout");
			errDialog.setContentText(errMsg);
			errDialog.showAndWait();	
		}
	}
	
    @FXML
    private void annulerHandler()
    {	
		addStage.close();
	}
    
    public void setAddStage(Stage addStage)
    {
    	this.addStage = addStage;
    	errDialog.initOwner(addStage);
    	infoDialog.initOwner(addStage);
    }
    
    public void setPromotion(Promotion promotion)
    {
    	this.promotion = promotion;
    }
    
}
