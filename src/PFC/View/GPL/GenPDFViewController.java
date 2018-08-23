package PFC.View.GPL;

import java.awt.Desktop;
import java.io.File;

import org.controlsfx.control.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Main;
import PFC.Model.Planning;

public class GenPDFViewController {

	private Stage genStage;
	private Planning planning;
	
	@FXML
	private TextField fileName;
	@FXML
	private TextField destination;
	@FXML
	private ChoiceBox<String> faculte;
	@FXML
	private ChoiceBox<String> departement;
	@FXML
	private ChoiceBox<String> session;
	
	@FXML
	private void initialize()
	{	
		ObservableList<String> listFaculte = FXCollections.observableArrayList();
		listFaculte.addAll("Faculté Sciences Exactes");
		faculte.setItems(listFaculte);
		faculte.getSelectionModel().select(0);
		
		ObservableList<String> listDeparetement = FXCollections.observableArrayList();
		listDeparetement.addAll("Département de Mathématiques","Département d'Informatique",
		 "Département Recherche Opérationnelle...","Département Chimie","Département de Physique");
		departement.setItems(listDeparetement);
		departement.getSelectionModel().select(0);
		
		ObservableList<String> listSession = FXCollections.observableArrayList();
		listSession.addAll("Session 1","Session 2");
		session.setItems(listSession);
		session.getSelectionModel().select(0);
	}
	
	@FXML
	private void valider() {
		
		String err = "";
		boolean valide = true;
		
		if(fileName.getText() == null || !fileName.getText().matches("[a-zA-Z0-9]+"))
		{
			err += "Nom du fichier non valide \n";
			valide = false;
		}
		if(destination.getText() == null || destination.getText().isEmpty())
		{
			err += "Aucune destination d'enregistrement na etait choisi \n";
			valide = false;
		}
		
		if(valide)
		{
			try {
				Planning.generateTexFile(planning, faculte.getValue(),departement.getValue(), session.getValue());
				 File f = Planning.generatePDF();
				if(f.exists())
				{
					File newFile = new File(destination.getText()+File.separator+fileName.getText()+".pdf");
					Notifications.create()
		             .title("Generation de planning")
		             .text("Le planning a bien etait enregistrer")
		             .darkStyle()
		             .position(Pos.BOTTOM_RIGHT)
		             .show();
					if(f.renameTo(newFile))
					{
						Desktop.getDesktop().open(newFile);
					}
					else
					{
						Desktop.getDesktop().open(f);
					}
					genStage.close();
					
				}
			} catch (Exception e) {
				Main.showExceptionDialog(e);
			}
		}
		else 
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.getDialogPane().getStylesheets().add("alert.css");
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(genStage);
			alert.setTitle("Message d'erreur");
			alert.setHeaderText("Informations non valide");
			alert.setContentText(err);
			alert.showAndWait();
		}
	}
	
	@FXML 
	private void annuler()
	{
		genStage.close();
	}
	
	@FXML 
	private void directoryChooserHandler()
	{
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Chemin d'enregistrement du fichier");
		
		File selectedFile = dirChooser.showDialog(genStage);
		
		if(selectedFile != null)
		{
			destination.setText(selectedFile.getAbsolutePath());
		}
	}
	
	public void setGenStage(Stage genStage)
	{
		this.genStage = genStage;
	}
	
	public void setPalnning(Planning planning)
	{
		this.planning = planning;
	}
	
}
