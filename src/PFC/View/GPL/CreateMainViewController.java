package PFC.View.GPL;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import PFC.ControlledScreen;
import PFC.Main;
import PFC.ScreensController;
import PFC.Model.BDDConnector;
import PFC.Model.Module;
import PFC.Model.Planning;
import PFC.Model.Promotion;

public class CreateMainViewController implements Initializable,ControlledScreen {
	
	private Main main;
	private ScreensController myController;
	private Connection connection = BDDConnector.getInstance();
	
	@FXML 
	private ChoiceBox<Promotion> promotionChoice;
	@FXML
	private ChoiceBox<String> semestreChoice;
	@FXML
	private DatePicker dateDebut;
	@FXML
	private DatePicker dateFin;
	
	@FXML
	private void backButtonHandler()
	{
		myController.setScreen(Main.mainScreen);
	}
	@FXML
	private void keysHandler(KeyEvent event)
	{
		if(event.getCode() == KeyCode.BACK_SPACE)
		{
			myController.setScreen(Main.mainScreen);
		}
	}
	@FXML
	private void nextStepButtonHandler()
	{
		String errMsg = "";
		boolean valide = true;
	    
		if(promotionChoice.getValue() == null)
	    {
	    	valide = false;
	    	errMsg += "Aucune promotion n'a été sélectionnée! \n";
	    }
		if(dateDebut.getValue() == null)
		{
			valide = false;
			errMsg += "La date de debut n'est pas specifier! \n"; 
		}
		if(dateFin.getValue() == null)
		{
			valide = false;
			errMsg += "La date de fin n'est pas specifier! \n"; 
		}
		if(valide)
		{
			Planning planning = new Planning();
			planning.setPromotion(promotionChoice.getValue());
			planning.setSemestre(semestreChoice.getValue());
			planning.setDateDebut(dateDebut.getValue());
			planning.setDateFin(dateFin.getValue());
			
			try {
				PreparedStatement ps = connection.prepareStatement("select * from planning where idPromotion = ?");
				ps.setString(1,promotionChoice.getValue().getIdentifiant());
				ResultSet result = ps.executeQuery();
				if(result.first())
				{
					Alert warDialog = new Alert(AlertType.WARNING);
					warDialog.initModality(Modality.APPLICATION_MODAL);
					warDialog.initOwner(main.getPrimaryStage());
					warDialog.setTitle("creation d'un planning - Avertissement");
					warDialog.setHeaderText("un planning existe deja pour cette promotion");
					warDialog.setContentText("voulez vous ecraser le planning deja existant");
					warDialog.getDialogPane().getStylesheets().add("alert.css");
					ButtonType ouiButton = new ButtonType("Oui");
					ButtonType nonButton = new ButtonType("Non");
					warDialog.getButtonTypes().setAll(ouiButton,nonButton);
					BorderPane sceneContent = (BorderPane) main.getPrimaryStage().getScene().getRoot();
					StackPane parent = (StackPane) sceneContent.getCenter();
			        Node content = parent.getChildren().get(1);
			        content.toBack();
					Optional<ButtonType> reponse = warDialog.showAndWait();
					content.toFront();
					if(reponse.get() == ouiButton)
					{
						Planning.supprimer(planning);
						main.setActivePlanning(planning);
						main.createPlanningView();
					}
					else 
					{
						myController.setScreen(Main.mainScreen);
					}
				}
				else 
				{
					main.setActivePlanning(planning);
					main.createPlanningView();
				}
			}
			catch(SQLException e)
			{
				Main.showExceptionDialog(e);
			}
		}
		else
		{
			final String title = "Paramètre du planning - Erreur";
			final String header = "Erreur de parametrage";
			final String msg = errMsg;
			main.showAlert(AlertType.ERROR,title,header, msg);
		}
	}
	public void setMain(Main main)
	{
		this.main = main;
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		this.myController = screenPage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		promotionChoice.valueProperty().addListener((obs,oldValue,newValue) -> {
			
			if(newValue != null)
			{
				PreparedStatement ps;
				try {
					ps = connection.prepareStatement("select distinct semestre from module where idPromotion = ?");
					ps.setString(1,newValue.getIdentifiant());
					ResultSet result = ps.executeQuery();
					semestreChoice.getItems().clear();
					while(result.next())
					{
						semestreChoice.getItems().add(result.getString("semestre"));
					}
					semestreChoice.getSelectionModel().select(0);
					ps.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Main.showExceptionDialog(e);
				}
			}
			else 
			{
				semestreChoice.setItems(FXCollections.observableArrayList());
			}
		});
	}
	
	@Override
	public void viewed() {
		
		try {
			
			ObservableList<Promotion> listPromotions = FXCollections.observableArrayList();
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery("select * from promotion where idPromotion in "
																	+ "(select distinct idPromotion from module)");
			Promotion p;
			while(result.next())
			{
				p = new Promotion();
				p.setIdentifiant(result.getString("idPromotion"));
				p.setNbSec(result.getInt("nbSections"));
				p.setNbGroupes(result.getInt("nbGroupes"));
				p.setModulesList(Module.consulter(p,null));
				listPromotions.add(p);
			}
			state.close();
			
			promotionChoice.setItems(listPromotions);
			
			if(listPromotions.size() > 0)
			{
				promotionChoice.getSelectionModel().select(0);
			}
			
		} catch (Exception e) {
			Main.showExceptionDialog(e);
		}
	}

}
