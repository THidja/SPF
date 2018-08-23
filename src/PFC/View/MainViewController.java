package PFC.View;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import PFC.ControlledScreen;
import PFC.Main;
import PFC.ScreensController;
import PFC.Model.Planning;
public class MainViewController implements Initializable, ControlledScreen {
	
	private Main main;
	private ScreensController myController;
	@FXML
	private Button editButton;
	
	@FXML 
	private void geButtonHandler()
	{
		myController.setScreen(Main.geScreen);
	}
	@FXML 
	private void glButtonHandler()
	{
		myController.setScreen(Main.glScreen);
	}
	@FXML 
	private void gpButtonHandler()
	{
		myController.setScreen(Main.gpScreen);
	}
	@FXML
	private void gcButtonHandler()
	{
		myController.setScreen(Main.gcScreen);
	}
	@FXML 
	private void gplCreateButtonHandler()
	{
		main.createCreateGPLStage();
	}
	@FXML
	private void gplEditButtonHandler()
	{
		main.createPlanningEditView();
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
		editButton.disableProperty().bind(Planning.emptyProperty());
	}
	
	@Override
	public void viewed() {
		// TODO Auto-generated method stub
		
	}
}
