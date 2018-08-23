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

public class GCEditController{
	
	private Stage editStage;
	private Alert errDialog;
	private Alert infoDialog;
	private ObservableList<Local> localList = FXCollections.observableArrayList();
	private Creneau creneau;
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
			errDialog.setTitle("Gestion des Creneaux - Erreurs");
			errDialog.setHeaderText("Erreur de modification");
			errDialog.getDialogPane().getStylesheets().add("alert.css");
			
			infoDialog = new Alert(AlertType.INFORMATION);
			infoDialog.setGraphic(new ImageView("Images/valider.png"));
			infoDialog.setTitle("Gestion des Creneaux - Informations");
			infoDialog.setHeaderText("Modification effectuée avec succés");
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
		editStage.close();
	}
    @FXML 
    private void chooseLocauxButtonHandler()
    {
    	GCLocauxChooser glc = new GCLocauxChooser();
    	glc.initOwner(editStage);
    	glc.setOnShown((event) -> {
   			glc.setX(editStage.getX() + editStage.getWidth() / 2 - glc.getWidth() / 2); 
   			glc.setY(editStage.getY() + editStage.getHeight() / 2 - glc.getHeight() / 2);
		});
    	glc.initModality(Modality.APPLICATION_MODAL);
    	glc.setDate(this.dateField.getValue());
    	LocalTime timeValue = DataOperations.StringToLocalTime(this.timeField.getText()); 
    	glc.setHeure(timeValue);
    	glc.refrechLocaux();
    	if(dateField.getValue().equals(creneau.getDate()) && timeValue.equals(creneau.getheure()))
    	{
    		glc.addItems(creneau.getLocalList());
       	}	
    	glc.setListContent(glc.getItems(),localList);
    	glc.showAndWait();
    	localList.setAll(glc.getCheckedElements());
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
			erreurMsg += "Aucune date n'a été définie! \n";
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
		if(localList == null || localList.size() == 0 )
		{
			valide = false;
			erreurMsg += "Aucun local n'a été choisi! \n";
		}	
		
		if(valide)
		{   
			boolean existInTheTable = false;
			boolean chovochement = false;
			String errMsg="";
			Creneau newCreneau = new Creneau();
			newCreneau.setPromotion(choosedPromotion);
			newCreneau.setDate(dateFieldValue);
			newCreneau.setheure(timeValue);
			newCreneau.setLocalList(localList);
			for(Creneau c : creneauList)
			{
				if(c.equals(newCreneau) && !c.equals(creneau))
				{
					errMsg = "Un créneau avec le méme horaire éxiste déja pour cette promotion!\n";
					existInTheTable = true;
				}
				else if((c.getDate().equals(newCreneau.getDate()) && c.getheure().equals(newCreneau.getheure())) && !c.equals(creneau))
				{
					for(Local l : c.getLocalList())
					{
						if(newCreneau.getLocalList().contains(l))
						chovochement = true;
					}
				}
			}
			
			if(existInTheTable)
			{
				errDialog.setContentText(errMsg);
				errDialog.showAndWait();
			}else if(chovochement && !existInTheTable)
			    {
				   errMsg = "chevauchement des locaux!,veuillez redéfinir la liste";
				   errDialog.setContentText(errMsg);
				   errDialog.showAndWait();
				   this.chooseLocauxButtonHandler();
			    }
			else 
			{
				try {
					Creneau.modifier(creneau,newCreneau);
					creneau.setDate(newCreneau.getDate());
					creneau.setheure(newCreneau.getheure());
					creneau.getLocalList().setAll(newCreneau.getLocalList());
					creneau.getPromotion().identifiantProperty().set(newCreneau.getPromotion().getIdentifiant());
					infoDialog.setContentText("Le créneau a bien été modifié");
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
    
    public void setEditStage(Stage editStage)
    {
    	this.editStage = editStage;
    	errDialog.initOwner(editStage);
    	infoDialog.initOwner(editStage);
 
    }
    
    public void setCreneauList(ObservableList<Creneau> creneauList)
    {
    	this.creneauList = creneauList;
    }
    
    public void setCreneau(Creneau creneau)
    {
    	this.creneau = creneau;
    	promotionChoice.setValue(creneau.getPromotion());
    	dateField.setValue(creneau.getDate());
    	timeField.setText(creneau.getheure().toString());
    	localList.setAll(creneau.getLocalList());
  
    }
    

}