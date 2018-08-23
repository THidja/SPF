package PFC.View.GL;

import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Local;
import PFC.Util.DataOperations;


public class GLEditController {
	
	
	private Local local;
	private Stage editStage;
	private Alert errDialog;
	private Alert infoDialog;
	private ObservableList<Local> localsList;
	
	@FXML 
	private TextField identTextField;
	@FXML
	private TextField capaciteTextField;
	
	@FXML
	private void initialize()
	{
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des locaux - Erreurs");
		errDialog.setHeaderText("Erreur de modification");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des locaux - Informations");
		infoDialog.setHeaderText("Modification effectuée avec succés");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
	}
	@FXML
	private void validerHandler()
	{
		String identReg = "([a-zA-Z ]{1,} ?[0-9 ]*)+";
		String numberReg = "[1-9 ]{1}[0-9 ]*";
		String identTextFieldValue = identTextField.getText();
		String capaciteTextFieldValue = capaciteTextField.getText();
		String erreurMsg = "";
		boolean valide = true ;
		
		if(!identTextFieldValue.matches(identReg) || identTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "l'identifiant n'est pas valide! \n";
			
		}
		else 
		{
			identTextFieldValue = identTextFieldValue.toLowerCase();
			identTextFieldValue = DataOperations.getValideName(identTextFieldValue);
			identTextFieldValue = DataOperations.capitalize(identTextFieldValue);
		}
		if(!capaciteTextFieldValue.matches(numberReg) || capaciteTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "la Capacité n'est pas valide!";
		}
		else 
		{
			capaciteTextFieldValue = DataOperations.getValideName(capaciteTextFieldValue);
		}
		if(valide)
		{
			boolean existInTheTable = false;
			
			Local newLocal = new Local();
			newLocal.setIdentifiant(identTextFieldValue);
			newLocal.setCapacite(Integer.valueOf(capaciteTextFieldValue));
			
			for(Local l : localsList)
			{
				if(l.equals(newLocal) && !l.equals(local))
				{
					existInTheTable = true;
				}
			}
			
			if(existInTheTable)
			{
				errDialog.setContentText("le local \""+identTextFieldValue+"\" éxiste déja");
				errDialog.showAndWait();
			}
			else 
			{

				try {
					Local.modifier(local,newLocal);
					local.setIdentifiant(newLocal.getIdentifiant());
					local.setCapacite(newLocal.getCapacite());
					infoDialog.setContentText("le Local a bien été modifié");
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
	public void setLocal(Local local)
	{
		this.local = local;
		identTextField.setText(local.getIdentifiant());
		capaciteTextField.setText(String.valueOf(local.getCapacite()));
	}
	public void setLocalsList(ObservableList<Local> localsList)
	{
		this.localsList = localsList;
	}
	public void setEditStage(Stage editStage)
	{
		this.editStage = editStage;
		errDialog.initOwner(editStage);
		infoDialog.initOwner(editStage);
	}
	

}
