package PFC.View.GC;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Creneau;
import PFC.Model.Local;
import PFC.Model.Promotion;
import PFC.Util.DataOperations;

public class GCAddController {
	
	private Stage addStage;
	private Alert errDialog;
	private Alert infoDialog;
	//private GCLocauxChooser glc = new GCLocauxChooser();
	private ObservableList<Local> localList = FXCollections.observableArrayList();
	private ObservableList<Creneau> creneauList;
	
	@FXML
	private ChoiceBox<Promotion> promotionChoice;
	@FXML
	private DatePicker dateField;
	@FXML
	private TextField timeField;
	@FXML
	private Button glcButton;
	
	@FXML
	private void initialize()
	{
		try {
			promotionChoice.setItems(Promotion.consulter());
			promotionChoice.getSelectionModel().select(0);
			
			errDialog = new Alert(AlertType.ERROR);
			errDialog.initModality(Modality.APPLICATION_MODAL);
			errDialog.setTitle("Gestion des Créneaux - Erreurs");
			errDialog.setHeaderText("Erreur d'ajout");
			errDialog.getDialogPane().getStylesheets().add("alert.css");
			
			infoDialog = new Alert(AlertType.INFORMATION);
			infoDialog.setGraphic(new ImageView("Images/valider.png"));
			infoDialog.setTitle("Gestion des Créneaux - Informations");
			infoDialog.setHeaderText("Ajout effectué avec succés");
			infoDialog.getDialogPane().getStylesheets().add("alert.css");
			
			BooleanBinding boolbind = new BooleanBinding() {
				
				{
					super.bind(dateField.valueProperty(),timeField.textProperty());
				}

				@Override
				protected boolean computeValue() {
				 if(dateField.getValue() != null && timeField.getText().matches("((0|1)?[0-9]|2[0-4]):[0-9]{2}( )*"))
				 {
					 return false;
				 }
					return true;
				}
				
			};
			
			glcButton.disableProperty().bind(boolbind);
			
		} catch (SQLException e) {
			Main.showExceptionDialog(e);
		}
	}
	
    @FXML
    private void annulerHandler()
    {	
		addStage.close();
	}
    @FXML 
    private void chooseLocauxButtonHandler()
    {
    	GCLocauxChooser glc = new GCLocauxChooser();
    	glc.initOwner(addStage);
    	glc.initModality(Modality.APPLICATION_MODAL);
    	glc.setOnShown((event) -> {
	   			glc.setX(addStage.getX() + addStage.getWidth() / 2 - glc.getWidth() / 2); 
	   			glc.setY(addStage.getY() + addStage.getHeight() / 2 - glc.getHeight() / 2);
			});
    	glc.setHeure(DataOperations.StringToLocalTime(timeField.getText()));
    	glc.setDate(dateField.getValue());
    	glc.refrechLocaux();
    	glc.setListContent(glc.getItems(), localList);
    	//glc.checkElements(localList);
    	glc.showAndWait();
    	localList.setAll(glc.getCheckedElements());
    	glc.close();
    }
    
    @FXML
    private void validerHandler()
	{
    	String timeReg = "((0|1)?[0-9]|2[0-4]):[0-9]{2}( )*";
	    String erreurMsg = "";
		boolean valide = true ;
		LocalDate dateFieldValue = dateField.getValue();
		Promotion choosedPromotion = promotionChoice.getSelectionModel().getSelectedItem();
	    String timeFieldValue = timeField.getText();
	    LocalTime timeValue = null;
		
	    if(promotionChoice.getValue() == null)
	    {
	    	valide = false;
	    	erreurMsg += "Aucune promotion n'a été sélectionnée! \n";
	    }
		if(dateFieldValue == null) 
		{
			valide = false;
			erreurMsg += "Aucune date n’a été définie! \n";
		}
		if(!timeFieldValue.matches(timeReg))
		{
			valide = false;
			erreurMsg += "L'heure n'est pas valide! \n";
		}
		else 
		{
			timeValue = DataOperations.StringToLocalTime(timeFieldValue);
		}
		if(localList == null || localList.size() == 0)
		{
			valide = false;
			erreurMsg += "Aucun local n'a été choisi! \n";
		}
		
		if(valide)
		{   
			boolean existInTheTable = false;
			
			Creneau creneau = new Creneau();
			creneau.setPromotion(choosedPromotion);
			creneau.setDate(dateFieldValue);
			creneau.setheure(timeValue);
			creneau.setLocalList(localList);
			
			for(Creneau c : creneauList)
			{
				
				if(c.equals(creneau))
				{
					existInTheTable = true;	
				}
			}
			
			if(existInTheTable)
			{
				String errMsg = "";
				errMsg += "Un créneau avec le méme horaire éxiste déja!";	
				errDialog.setContentText(errMsg);
				errDialog.showAndWait();
			}
			else 
			{
				
				try {
					
					Creneau.ajouter(creneau);
					creneauList.add(creneau);
					infoDialog.setContentText("Le créneau a bien été ajouté");
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
    	/*glc.initOwner(addStage);
    	glc.setOnShown((event) -> {
	   			glc.setX(addStage.getX() + addStage.getWidth() / 2 - glc.getWidth() / 2); 
	   			glc.setY(addStage.getY() + addStage.getHeight() / 2 - glc.getHeight() / 2);
			});*/
    }
    
    public void setCreneauList(ObservableList<Creneau> creneauList)
    {
    	this.creneauList = creneauList;
    }
}
