package PFC.View.GP;

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
import PFC.Model.Promotion;
import PFC.Util.DataOperations;

public class GPAddController {
	
	
	private Stage addStage;
	private ObservableList<Promotion> promotionsList;
	private Alert errDialog;
	private Alert infoDialog;
	
	@FXML 
	private TextField identTextField;
	@FXML
	private TextField nbSecTextField;
	@FXML
	private TextField nbGroupesTextField;
	@FXML
	private TextField tailleG;
 	@FXML
	private ChoiceBox<String> semestre1;
	@FXML
	private ChoiceBox<String> semestre2;
	
	
	@FXML
	private void initialize()
	{
		errDialog = new Alert(AlertType.ERROR);
		errDialog.initModality(Modality.APPLICATION_MODAL);
		errDialog.setTitle("Gestion des promotions - Erreurs");
		errDialog.setHeaderText("Erreur d'ajout");
		errDialog.getDialogPane().getStylesheets().add("alert.css");
		
		infoDialog = new Alert(AlertType.INFORMATION);
		infoDialog.setGraphic(new ImageView("Images/valider.png"));
		infoDialog.setTitle("Gestion des promotions - Informations");
		infoDialog.setHeaderText("Ajout effectué avec succés");
		infoDialog.getDialogPane().getStylesheets().add("alert.css");
		
		String[] semestres1 = {"Semestre 1","Semestre 3","Semestre 5"};
		String[] semestres2 = {"semestre 2","Semestre 4","Semestre 6"};
		
		semestre1.setItems(FXCollections.observableArrayList(semestres1));
		semestre2.setItems(FXCollections.observableArrayList(semestres2));
		semestre1.getSelectionModel().select(0);
		semestre2.getSelectionModel().select(0);
		semestre1.selectionModelProperty().addListener((oldV,newV,observable) -> {
			semestre2.getSelectionModel().select(semestre1.getSelectionModel().getSelectedIndex());
			System.out.println(semestre1.getSelectionModel().getSelectedItem());
		});
	}
	@FXML
	private void validerHandler()
	{
		String identReg = "[a-zA-Z ]{1}[a-zA-Z0-9 ]*";
		String numberReg = "[1-9 ]{1}[ 0-9]*";
		String identTextFieldValue = identTextField.getText();
		String nbSecTextFieldValue = nbSecTextField.getText();
		String nbGroupesTextFieldValue = nbGroupesTextField.getText();
		String erreurMsg = "";
		boolean valide = true ;
		if(!identTextFieldValue.matches(identReg) || identTextFieldValue.isEmpty())
		{
			valide = false;
			erreurMsg += "L'identifiant n'est pas valide! \n";
			
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
			
			Promotion promotion = new Promotion();
			promotion.setIdentifiant(identTextFieldValue);
			promotion.setNbSec(Integer.valueOf(nbSecTextFieldValue));
			promotion.setNbGroupes(Integer.valueOf(nbGroupesTextFieldValue));
			
			for(Promotion p : promotionsList)
			{
				if(p.equals(promotion))
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
					Promotion.ajouter(promotion);
					promotionsList.add(promotion);
					infoDialog.setContentText("La promotion \""+identTextFieldValue+"\" a bien été ajoutée");
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
	
	public void cancelHandler()
	{
		addStage.close();
	}
	
	public void setPromotionsList(ObservableList<Promotion> promotionsList)
	{
		this.promotionsList = promotionsList;
	}
	public void setAddStage(Stage addStage)
	{
		this.addStage = addStage;
		errDialog.initOwner(addStage);
		infoDialog.initOwner(addStage);
	}
	

}
