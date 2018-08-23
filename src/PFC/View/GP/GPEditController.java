package PFC.View.GP;

import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Promotion;
import PFC.Util.DataOperations;

public class GPEditController {
	
	
	private Promotion promotion;
	private ObservableList<Promotion> promotionsList;
	private Stage editStage;
	private Alert errDialog;
	private Alert infoDialog;
	
	@FXML 
	private TextField identTextField;
	@FXML
	private TextField nbSecTextField;
	@FXML
	private TextField nbGroupesTextField;
	
	@FXML
	private void initialize()
	{
		errDialog = new Alert(AlertType.ERROR);
		errDialog.setTitle("Gestion des promotions - Erreurs");
		errDialog.setHeaderText("Erreur de modification");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des promotions - Informations");
		infoDialog.setHeaderText("Modifiaction effectué avec succés");
		infoDialog.setContentText("La promotion a bien été modifiée");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
	}
	
	@FXML
	private void validerHandler()
	{
		String identReg = "[a-zA-Z ]{1}( ?[a-zA-Z0-9 ]{1,})*";
		String numberReg = "[1-9 ]{1}[0-9 ]*";
		String identTextFieldValue = identTextField.getText();
		String nbSecTextFieldValue = nbSecTextField.getText();
		String nbGroupesTextFieldValue = nbGroupesTextField.getText();
		String erreurMsg = "";
		boolean valide = true ;
		
		if(!identTextFieldValue.matches(identReg) || identTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "L'identifiant n'est pas valide!\n";	
		}
		else 
		{
			identTextFieldValue = identTextFieldValue.toLowerCase();
			identTextFieldValue = DataOperations.getValideName(identTextFieldValue);
			identTextFieldValue = DataOperations.capitalize(identTextFieldValue);
		}
		if(!nbSecTextFieldValue.matches(numberReg) || nbSecTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "Le nombre de sections n'est pas valide! \n";
		}
		else
		{
			nbSecTextFieldValue = DataOperations.getValideName(nbSecTextFieldValue);
		}
		if(!nbGroupesTextFieldValue.matches(numberReg) || nbGroupesTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "Le nombre de groupes n'est pas valide! \n";
		}
		else 
		{
			nbGroupesTextFieldValue = DataOperations.getValideName(nbGroupesTextFieldValue);
		}
		
		if(valide)
		{
			boolean existInTheTable = false;
			
			Promotion newPromotion = new Promotion();
			newPromotion.setIdentifiant(identTextFieldValue);
			newPromotion.setNbSec(Integer.valueOf(nbSecTextFieldValue));
			newPromotion.setNbGroupes(Integer.valueOf(nbGroupesTextFieldValue));
			
			for(Promotion p : promotionsList)
			{
				if(p.equals(newPromotion) && !p.equals(promotion))
				{
					existInTheTable = true;
				}
			}
			
			if(existInTheTable)
			{
				errDialog.setContentText("la pomotion \""+identTextFieldValue+"\" éxiste déja");
				errDialog.showAndWait();
			}
			else 
			{

				try {
					Promotion.modifier(promotion,newPromotion);
					promotion.setIdentifiant(newPromotion.getIdentifiant());
					promotion.setNbSec(newPromotion.getNbSec());
					promotion.setNbGroupes(newPromotion.getNbGroupes());
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
	
	public void cancelHandler()
	{
		editStage.close();
	}
	
	
	public void setPromotion(Promotion promotion)
	{
		this.promotion = promotion;
		identTextField.setText(promotion.getIdentifiant());
		nbSecTextField.setText(String.valueOf(promotion.getNbSec()));
		nbGroupesTextField.setText(String.valueOf(promotion.getNbGroupes()));
	}
	public void setPromotionsList(ObservableList<Promotion> promotionsList)
	{
		this.promotionsList = promotionsList;
	}
	public void setEditStage(Stage editStage)
	{
		this.editStage = editStage;
		errDialog.initOwner(editStage);
		infoDialog.initOwner(editStage);
	}
}
