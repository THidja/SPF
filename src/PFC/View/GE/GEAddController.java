package PFC.View.GE;

import java.sql.SQLException;

import PFC.Main;
import PFC.Model.Enseignant;
import PFC.Util.DataOperations;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GEAddController {
	
	private Stage addStage;
	private Alert errDialog;
	private Alert infoDialog;
	private ObservableList<Enseignant> enseignantsList;
	
	@FXML 
	private TextField nomTextField;
	@FXML
	private TextField prenomTextField;
	@FXML
	private ChoiceBox<String> gradeChoiceBox;
	
	@FXML
	private void initialize()
	{	
		gradeChoiceBox.getItems().addAll("Vacataire","MAB","MAA","MCB","MCA","PR");
		gradeChoiceBox.getSelectionModel().select(0);
		
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des Enseignants - Erreurs");
		errDialog.setHeaderText("Erreur d'ajout");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des Enseignants - Informations");
		infoDialog.setHeaderText("Ajout effectué avec succés");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
	}
    @FXML
    private void annulerHandler()
    {	
		addStage.close();
	}
	
    @FXML
    private void validerHandler()
	{
		final String npReg = "[a-zA-Z ]+";
		final String emptyReg = " *";
	    String erreurMsg = "";
		String nomTextFieldValue = nomTextField.getText();
	    String prenomTextFieldValue = prenomTextField.getText();
		boolean valide = true ;
		
		if(!nomTextFieldValue.matches(npReg) || nomTextFieldValue.matches(emptyReg)) 
		{
			valide = false;
			erreurMsg += "Le nom n'est pas valide!\n";
		}
		else 
		{
			nomTextFieldValue = nomTextFieldValue.toUpperCase();
			nomTextFieldValue = DataOperations.getValideName(nomTextFieldValue);
		}
		if(!prenomTextFieldValue.matches(npReg) || prenomTextFieldValue.matches(emptyReg)) 
		{
			valide = false;
			erreurMsg += "Le prenom n'est pas valide!\n";
		}
		else
		{	
			prenomTextFieldValue = prenomTextFieldValue.toLowerCase();
			prenomTextFieldValue = DataOperations.getValideName(prenomTextFieldValue);
			prenomTextFieldValue = DataOperations.capitalize(prenomTextFieldValue);
		}
		
		if(valide)
		{   
			boolean existInTheTable = false;
			
			Enseignant enseignant = new Enseignant();
		    enseignant.setNom(nomTextFieldValue);
		    enseignant.setPrenom(prenomTextFieldValue);
			enseignant.setGrade(gradeChoiceBox.getSelectionModel().selectedItemProperty().get());
			
			for(Enseignant e : enseignantsList)
			{
				if(e.equals(enseignant))
				{
					existInTheTable = true;
				}
			}
			
			if(existInTheTable)
			{
				errDialog.setContentText("L'enseignant \""+nomTextFieldValue+" "+prenomTextFieldValue+"\" éxiste déja");
				errDialog.showAndWait();
			}
			else 
			{
				try {
					Enseignant.ajouter(enseignant);
					enseignantsList.add(enseignant);
					infoDialog.setContentText("L'enseignant \""+nomTextFieldValue+" "+prenomTextFieldValue
							                   + "\" a bien été ajouté");
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
			errDialog.setContentText(erreurMsg);
			errDialog.showAndWait();
		}
	}
	
	public void setAddStage(Stage addStage)
	{
		this.addStage = addStage;
		errDialog.initOwner(addStage);
		infoDialog.initOwner(addStage);
	}
	public void setEnseignantsList(ObservableList<Enseignant> enseignantsList)
	{
		this.enseignantsList=  enseignantsList;
		
	}
}
