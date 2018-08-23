package PFC.View.GE;

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
import PFC.Util.DataOperations;

public class GEEditController {
	
	private Enseignant enseignant;
	private ObservableList<Enseignant> enseignatsList;
	private Stage editStage;
	private Alert errDialog;
	private Alert infoDialog;
	
	@FXML 
	private TextField nomTextField;
	@FXML
	private TextField prenomTextField;
	@FXML
	private ChoiceBox<String> gradeChoiceBox;

	
	@FXML
	private void initialize()
	{	
		gradeChoiceBox.getItems().addAll("Vacataire","MAA","MAB","MCA","MCB","PR");
		gradeChoiceBox.getSelectionModel().select(0);
        
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des Enseignants - Erreurs");
		errDialog.setHeaderText("Erreur de modification");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des Enseignants - Informations");
		infoDialog.setHeaderText("Modification effectuée avec succés");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
	}
	
	@FXML
	private void validerHandler()
	{
		final String npReg = "[a-zA-Z ]+";
		final String emptyReg = " *";
	    String erreurMsg = "";
		String nomTextFieldValue = nomTextField.getText();
	    String prenomTextFieldValue = prenomTextField.getText();
	    boolean valide = true;
		
		if(!nomTextFieldValue.matches(npReg) || nomTextFieldValue.matches(emptyReg)) 
		{
			valide = false;
			erreurMsg += "Le nom n'est pas valide! \n";
		}
		else 
		{
			nomTextFieldValue = nomTextFieldValue.toUpperCase();
			nomTextFieldValue = DataOperations.getValideName(nomTextFieldValue);
		}
		if(!prenomTextFieldValue.matches(npReg) || prenomTextFieldValue.matches(emptyReg)){
			valide = false;
			erreurMsg += "Le prenom n'est pas valide! \n";
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
			
			Enseignant newEnseignant = new Enseignant();
			newEnseignant.setNom(nomTextFieldValue);
			newEnseignant.setPrenom(prenomTextFieldValue);
			newEnseignant.setGrade(gradeChoiceBox.getSelectionModel().selectedItemProperty().get());
			
			for(Enseignant e : enseignatsList)
			{
				if(e.equals(newEnseignant) && !e.equals(enseignant))
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
					Enseignant.modifier(enseignant,newEnseignant);
					enseignant.setNom(newEnseignant.getNom());
					enseignant.setPrenom(newEnseignant.getPrenom());
					enseignant.setGrade(newEnseignant.getGrade());
					infoDialog.setContentText("L'enseignant a bien été modifié");
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
			errDialog.setContentText(erreurMsg);
			errDialog.showAndWait();
		}
	}
	@FXML
	private void annulerHandler(){
		editStage.close();
	}
	public void setEnseignant(Enseignant enseignant)
	{
		this.enseignant = enseignant;
		nomTextField.setText(enseignant.getNom());
		prenomTextField.setText(enseignant.getPrenom());
		gradeChoiceBox.getSelectionModel().select(enseignant.getGrade());
		
	}
	public void setEnseignantsList(ObservableList<Enseignant> enseignantsList)
	{
		this.enseignatsList = enseignantsList;
	}
	public void setEditStage(Stage editStage)
	{
		this.editStage = editStage;
		errDialog.initOwner(editStage);
		infoDialog.initOwner(editStage);
	}
	

}
