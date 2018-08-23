package PFC.View.GP.GM;

import java.sql.SQLException;

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

public class GMEditController {
	
	private Stage editStage;
	private Alert errDialog;
	private Alert infoDialog;
	
	private Promotion promotion;
	private Module module;
	private ObservableList<Enseignant> listEnseignant;
	
	
	@FXML
	private TextField inputField;
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
		errDialog.setHeaderText("Erreur de modification");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des Modules - Information");
		infoDialog.setHeaderText("Modification effectuée avec succés");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
		
	}
	
	
	@FXML
	private void ecButtonHandler() throws SQLException
	{
		GMEnsChooser ec = new GMEnsChooser();
		ec.initModality(Modality.APPLICATION_MODAL);
		ec.initOwner(editStage);
		
		ec.setListContent(ec.getListEnseignant(),listEnseignant);
    	ec.showAndWait();
    	listEnseignant.setAll(ec.getCheckedElements());
	
	}
	
    @FXML
    private void annulerHandler()
    {	
		editStage.close();
	}

    @FXML
    private void validerHandler()
	{
    	
    String inputFieldValue = inputField.getText();
    
	boolean valide = true;
	String errMsg = "";
	
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
		errMsg += "Attention,vous devez définir au moins un chargé du module!\n";
	}
	
	if(valide)
	{
		Module newModule = new Module();
		newModule.setItitule(inputFieldValue);
		newModule.setSemestre(semestreChoice.getValue());
		newModule.setListEnseignant(listEnseignant);
		
		boolean existInTheModulesList = false;
		for(Module m : promotion.getModulesList())
		{
			if(newModule.equals(m) && !newModule.equals(module))
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
    			module.setListEnseignant(listEnseignant);
        		infoDialog.setHeaderText("Modification effectuée avec succés ");
        		infoDialog.setContentText("Le module a bien été modifié");
        		infoDialog.showAndWait();
        		editStage.close();
    		}
    		catch(SQLException e)
    		{
    			Main.showExceptionDialog(e);
    		}

		}
	}
	else 
	{
		errDialog.setHeaderText("Erreur de modification");
		errDialog.setContentText(errMsg);
		errDialog.showAndWait();	
	}
	}
	
    public void setEditStage(Stage editStage)
    {
    	this.editStage = editStage;
    	errDialog.initOwner(editStage);
    	infoDialog.initOwner(editStage);
    }
    
    public void setPromotion(Promotion promotion)
    {
    	this.promotion = promotion;
    }
    
    public void setModule(Module module)
    {
    	this.module = module;
    	inputField.setText(module.getIntitule());
    	semestreChoice.setValue(module.getSemestre());
    	listEnseignant = module.getListEnseignant();
    }
}
