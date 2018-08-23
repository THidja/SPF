package PFC;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import PFC.Model.BDDConnector;
import PFC.Model.Creneau;
import PFC.Model.Enseignant;
import PFC.Model.Local;
import PFC.Model.Module;
import PFC.Model.Planning;
import PFC.Model.Promotion;
import PFC.View.GC.GCAddController;
import PFC.View.GC.GCEditController;
import PFC.View.GE.GEAddController;
import PFC.View.GE.GEEditController;
import PFC.View.GL.GLAddController;
import PFC.View.GL.GLEditController;
import PFC.View.GP.GPAddController;
import PFC.View.GP.GPEditController;
import PFC.View.GP.GM.GMAddController;
import PFC.View.GP.GM.GMEditController;
import PFC.View.GPL.GenPDFViewController;

public class Main extends Application {
	
	
	public final static String mainScreen = "main";
	public final static String mainScreenFile = "View/MainView.fxml";
	public final static String gcScreen = "gcScreen";
	public final static String gcScreenFile = "View/GC/GCMainView.fxml";
	public final static String geScreen = "geScreen";
	public final static String geScreenFile = "View/GE/GEMainView.fxml";
	public final static String glScreen = "glScreen";
	public final static String glScreenFile = "View/GL/GLMainView.fxml";
	public final static String gpScreen = "gpScreen";
	public final static String gpScreenFile = "View/GP/GPMainView.fxml";
	public final static String gmScreen = "gmScreen";
	public final static String gmScreenFile ="View/GP/GM/GMMainView.fxml";
	public final static String gplCreateScreen = "gplCreateScreen";
	public final static String gplCreateScreenFile = "View/GPL/CreateMainView.fxml";
	public final static String gplPlanningScreen = "gplPlanningScreen";
	public final static String gplPlanningScreenFile = "View/GPL/PlanningView.fxml";
	public final static String gplEditPlanningScreen = "gplEditPlanningScreen";
	public final static String gplEditPlanningScreenFile = "View/GPL/PlanningEditView.fxml";
	
	private Stage primaryStage;
	private ScreensController content;
	private Promotion activePromotion = new Promotion();
	private Planning activePlanning = new Planning();
	private Connection connection = BDDConnector.getInstance();
	
	private ObservableList<Local> listLocal= FXCollections.observableArrayList();
	private ObservableList<Enseignant> listEnseignant = FXCollections.observableArrayList();
	private ObservableList<Creneau> listCreneau = FXCollections.observableArrayList();
	private ObservableList<Promotion> listPromotion = FXCollections.observableArrayList();
	
	public Main() throws SQLException
	{
		refrechApplicationData();
		if(listPromotion.size() > 0)
		{
			this.activePromotion = listPromotion.get(0).clone();
		}
		
		//Planning.refrechEmptyStatut();
	}
	
	
	public void refrechApplicationData()
	{
		try {
			listLocal.setAll(Local.consulter());
			listEnseignant.setAll(Enseignant.consulter());
			//listCreneau.setAll(Creneau.consulter(null,null,null));
			//listPromotion.setAll(Promotion.consulter());
		}
		catch(Exception e)
		{
			Main.showExceptionDialog(e);
		}
	}
	
	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Exam Planner");
		
		content = new ScreensController(this);
		content.loadScreen(mainScreen,mainScreenFile);
		content.loadScreen(gcScreen, gcScreenFile);
		content.loadScreen(geScreen,geScreenFile);
		content.loadScreen(glScreen,glScreenFile);
		content.loadScreen(gpScreen,gpScreenFile);
		content.loadScreen(gmScreen,gmScreenFile);
		content.loadScreen(gplCreateScreen, gplCreateScreenFile);
		content.loadScreen(gplPlanningScreen, gplPlanningScreenFile);
		content.loadScreen(gplEditPlanningScreen, gplEditPlanningScreenFile);
		
		content.setScreen(mainScreen);
		
		BorderPane rootPane = new BorderPane();
		Image back = new Image("/PFC/View/background.jpg");
		rootPane.setBackground(new Background(new BackgroundImage(back,
																  BackgroundRepeat.REPEAT,
																  BackgroundRepeat.REPEAT,
																  BackgroundPosition.CENTER,
																  BackgroundSize.DEFAULT)));
		rootPane.setCenter(content);
		
		// Create Menu Items
		MenuBar menubar = new MenuBar();
			Menu fichier = new Menu("Fichier");
				MenuItem quitter = new MenuItem("Quitter");
			Menu affichage = new Menu("Affichage");
				MenuItem pleinEcran = new MenuItem("Mode plein ecran");
			Menu navigation = new Menu("Navigation");
				MenuItem accueil = new MenuItem("Tableau de Bord");
				Menu gr = new Menu("Gestion des ressources");
					MenuItem ge = new MenuItem("Gestion des enseignants");
					MenuItem gl = new MenuItem("Gestion des locaux");
					MenuItem gp = new MenuItem("Gestion des promotions");
					MenuItem gc = new MenuItem("Gestion des créneaux");
				Menu gpl = new Menu("Gestion des planning");
					MenuItem gpln = new MenuItem("Créer un planning");
					MenuItem gplm = new MenuItem("Modifier un planning");
					gplm.disableProperty().bind(Planning.emptyProperty());
		// Append Menu Items		
		menubar.getMenus().add(fichier);
			fichier.getItems().add(quitter);
		menubar.getMenus().add(affichage);
			affichage.getItems().add(pleinEcran);
		menubar.getMenus().add(navigation);
			navigation.getItems().add(accueil);
			navigation.getItems().add(gr);
				gr.getItems().add(ge);
				gr.getItems().add(gl);
				gr.getItems().add(gp);
				gr.getItems().add(gc);
			navigation.getItems().add(gpl);
				gpl.getItems().add(gpln);
				gpl.getItems().add(gplm);
		// Add Events To Menu Items
		quitter.setOnAction((event) -> this.closeListener());
		pleinEcran.setOnAction((event) -> this.fullScreenHandler());
		accueil.setOnAction((event) -> content.setScreen(mainScreen));
		ge.setOnAction((event) -> content.setScreen(geScreen));
		gl.setOnAction((event) -> content.setScreen(glScreen));
		gp.setOnAction((event) -> content.setScreen(gpScreen));
		gc.setOnAction((event) -> content.setScreen(gcScreen));
		gpln.setOnAction((event) -> content.setScreen(gplCreateScreen));
		gplm.setOnAction((event) -> this.createPlanningEditView());
		rootPane.setTop(menubar);
		// Other Associations To The Menu 
		menubar.getStylesheets().add("/PFC/View/design.css");
		menubar.getStyleClass().add("myMenu");
		quitter.setAccelerator(KeyCombination.keyCombination("CTRL+Q"));
		quitter.setGraphic(new ImageView("Images/quit.png"));
		pleinEcran.setAccelerator(KeyCombination.keyCombination("CTRL+F"));
		pleinEcran.setGraphic(new ImageView("Images/fullscreen.png"));
		
		
		this.primaryStage = primaryStage;
		primaryStage.getIcons().add(new Image("/Images/icon.png"));
		primaryStage.setOnCloseRequest(event -> { this.closeListener(); event.consume(); });
		primaryStage.centerOnScreen();
		Scene scene = new Scene(rootPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	// Listener for Window Close Button
	
	public void closeListener()
	{
		Alert exitConfirmDialog  = new Alert(AlertType.CONFIRMATION);
    	exitConfirmDialog.getDialogPane().getStylesheets().add("alert.css");
    	exitConfirmDialog.initOwner(this.primaryStage);
    	exitConfirmDialog.initModality(Modality.APPLICATION_MODAL);
    	exitConfirmDialog.setContentText("Voulez vous vraiment quitter le programme ?");
        ButtonType buttonTypeOui = new ButtonType("Oui",ButtonData.YES);
        ButtonType buttonTypeNon = new ButtonType("Non",ButtonData.NO);
        exitConfirmDialog.getButtonTypes().clear();
        exitConfirmDialog.getButtonTypes().addAll(buttonTypeOui,buttonTypeNon);
        BorderPane sceneContent = (BorderPane) primaryStage.getScene().getRoot();
        StackPane parent = (StackPane) sceneContent.getCenter();
        Node content = parent.getChildren().get(1);
        content.toBack();
    	Optional<ButtonType> result = exitConfirmDialog.showAndWait();
    	content.toFront();
    	if(result.get() == buttonTypeOui)
    	{
    		this.primaryStage.close();
    	}
	}
	
	// 
	
	public void fullScreenHandler()
	{
		this.getPrimaryStage().setFullScreen(!this.getPrimaryStage().isFullScreen());
	}
	
	public void showAlert(AlertType type,String title,String header,String msg)
	{
		Alert alert = new Alert(type);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(primaryStage);
		alert.getDialogPane().getStylesheets().add("alert.css");
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(msg);
        BorderPane sceneContent = (BorderPane) getPrimaryStage().getScene().getRoot();
        StackPane parent = (StackPane) sceneContent.getCenter();
        Node content = parent.getChildren().get(1);
        content.toBack();
		alert.showAndWait();
		content.toFront();
	}
	/*-----------------------------------------------Gestion des Crenaux Stages------------------------------------------------>*/

   	public void showGCAddDialog(ObservableList<Creneau> listCreneau)
   	{
   		Stage addDialog = new Stage();
   		addDialog.initModality(Modality.APPLICATION_MODAL);
   		addDialog.setResizable(false);
   		addDialog.initOwner(primaryStage);
   		addDialog.setTitle("Gestion des Creneaux - Ajouter un Creneau");
   		addDialog.getIcons().add(new Image("Images/add.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GC/GCAddView.fxml"));
   		try {
   			AnchorPane layoutPane =  loader.load();
   			Scene scene = new Scene(layoutPane);
   			addDialog.setScene(scene);
   			GCAddController controller = loader.getController();
   			controller.setAddStage(addDialog);
   			controller.setCreneauList(listCreneau);
   			addDialog.setOnShown((event) -> {
   	   			addDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - addDialog.getWidth() / 2); 
   	   			addDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - addDialog.getHeight() / 2);
   			});
   			addDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   		
   	}
   	
   	public void showGCEditDialog(ObservableList<Creneau> listCreneau,Creneau creneau)
   	{
   		Stage editDialog = new Stage();
   		editDialog.initModality(Modality.APPLICATION_MODAL);
   	    editDialog.setResizable(false);
   		editDialog.initOwner(primaryStage);
   		editDialog.setTitle("Gestion des crenéaux - Modifier un créneau");
   		editDialog.getIcons().add(new Image("Images/edit.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GC/GCEditView.fxml"));
   		try {
   			AnchorPane layoutPane =  loader.load();
   			Scene scene = new Scene(layoutPane);
   			editDialog.setScene(scene);
   			GCEditController controller = loader.getController();
   			controller.setEditStage(editDialog);
   			controller.setCreneauList(listCreneau);
   			controller.setCreneau(creneau);
   			editDialog.setOnShown((event) -> {
   	   			editDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - editDialog.getWidth() / 2); 
   	   			editDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - editDialog.getHeight() / 2);
   			});
   			editDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   		
   	}
   	
   	/*-----------------------------------------------Gestion des Enseignant Stages------------------------------------------------>*/
	
   	public void showGEAddDialog(ObservableList<Enseignant> listEnseignant)
   	{
   		Stage addDialog = new Stage();
   		addDialog.initModality(Modality.APPLICATION_MODAL);
   		addDialog.setResizable(false);
   		addDialog.initOwner(primaryStage);
   		addDialog.setTitle("Gestion des Enseignants - Ajouter un Enseigant");
   		addDialog.getIcons().add(new Image("Images/add.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GE/GEAddView.fxml"));
   		try {
   			AnchorPane layoutPane =  loader.load();
   			GEAddController controller = loader.getController();
   			Scene scene = new Scene(layoutPane);
   			addDialog.setScene(scene);
   			controller.setAddStage(addDialog);
   			controller.setEnseignantsList(listEnseignant);
   			addDialog.setOnShown((event) -> {
   	   			addDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - addDialog.getWidth() / 2); 
   	   			addDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - addDialog.getHeight() / 2);
   			});
   			addDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   	}

   	public void showGEEditDialog(ObservableList<Enseignant> listEnseignant,Enseignant enseignant)
   	{
   		Stage editDialog = new Stage();
   		editDialog.setResizable(false);
   		editDialog.initModality(Modality.APPLICATION_MODAL);
   		editDialog.initOwner(primaryStage);
   		editDialog.setTitle("Gestion des Enseignants - Modifier un Enseignant");
   		editDialog.getIcons().add(new Image("Images/edit.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GE/GEEditView.fxml"));
   		try {
   			AnchorPane layoutPane = loader.load();
   			GEEditController controller = loader.getController();
   			Scene scene = new Scene(layoutPane);
   			editDialog.setScene(scene);
   			controller.setEditStage(editDialog);
   			controller.setEnseignant(enseignant);
   			controller.setEnseignantsList(listEnseignant);
   			editDialog.setOnShown((event) -> {
   	   			editDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - editDialog.getWidth() / 2); 
   	   			editDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - editDialog.getHeight() / 2);
   			});
   			editDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   		
   	}

   	/*-----------------------------------------------Gestion des Locaux Stages------------------------------------------------>*/

	public void showGLAddDialog(ObservableList<Local> listLocal)
	{
		Stage addDialog = new Stage();
		addDialog.setResizable(false);
		addDialog.initModality(Modality.APPLICATION_MODAL);
		addDialog.initOwner(primaryStage);
		addDialog.setTitle("Gestion des locaux - Ajouter un Local");
		addDialog.getIcons().add(new Image("Images/add.png"));
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("View/GL/GLAddView.fxml"));
		try {
			AnchorPane layoutPane = (AnchorPane) loader.load();
			GLAddController controller = loader.getController();
			Scene scene = new Scene(layoutPane);
			addDialog.setScene(scene);
			controller.setAddStage(addDialog);
			controller.setlocalsList(listLocal);
			addDialog.setOnShown((event) -> {
	   			addDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - addDialog.getWidth() / 2); 
	   			addDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - addDialog.getHeight() / 2);
			});
   			addDialog.showAndWait();
		}
		catch(IOException e)
		{
			Main.showExceptionDialog(e);
		}
		
	}
	public void showGLEditDialog(ObservableList<Local> listLocal,Local local)
	{
		Stage editDialog = new Stage();
		editDialog.setResizable(false);
		editDialog.initModality(Modality.APPLICATION_MODAL);
		editDialog.initOwner(primaryStage);
		editDialog.setTitle("Gestion des locaux - Modifier un Local");
		editDialog.getIcons().add(new Image("Images/edit.png"));
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("View/GL/GLEditView.fxml"));
		try {
			AnchorPane layoutPane = (AnchorPane) loader.load();
			Scene scene = new Scene(layoutPane);
			editDialog.setScene(scene);
			GLEditController controller = loader.getController();
			controller.setEditStage(editDialog);
			controller.setLocal(local);
			controller.setLocalsList(listLocal);
			editDialog.setOnShown((event) -> {
	   			editDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - editDialog.getWidth() / 2); 
	   			editDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - editDialog.getHeight() / 2);
			});
   			editDialog.showAndWait();
		}
		catch(IOException e)
		{
			Main.showExceptionDialog(e);
		}
		
	}
	
	/*-----------------------------------------------Gestion des Promotion Stages------------------------------------------------>*/
	
	public void showGPAddDialog(ObservableList<Promotion> listPromotion)
	{
		Stage addDialog = new Stage();
		addDialog.setResizable(false);
		addDialog.initModality(Modality.APPLICATION_MODAL);
		addDialog.initOwner(primaryStage);
		addDialog.setTitle("Gestion des Promotions - Ajouter une promotion");
		addDialog.getIcons().add(new Image("Images/add.png"));
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("View/GP/GPAddView.fxml"));
		try {
			AnchorPane layoutPane = (AnchorPane) loader.load();
			GPAddController controller = loader.getController();
			Scene scene = new Scene(layoutPane);
			addDialog.setScene(scene);
			controller.setAddStage(addDialog);
			controller.setPromotionsList(listPromotion);
			addDialog.setOnShown((event) -> {
	   			addDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - addDialog.getWidth() / 2); 
	   			addDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - addDialog.getHeight() / 2);
			});
			addDialog.showAndWait();
		}
		catch(IOException e)
		{
			Main.showExceptionDialog(e);
		}
	}
	public void showGPEditDialog(ObservableList<Promotion> listPromotion,Promotion promotion)
	{
		Stage editDialog = new Stage();
		editDialog.setResizable(false);
		editDialog.initModality(Modality.APPLICATION_MODAL);
		editDialog.initOwner(primaryStage);
		editDialog.setTitle("Gestion des Promotions - Modifier une promotion");
		editDialog.getIcons().add(new Image("Images/edit.png"));
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("View/GP/GPEditView.fxml"));
		try {
			AnchorPane layoutPane = (AnchorPane) loader.load();
			GPEditController controller = loader.getController();
			Scene scene = new Scene(layoutPane);
			editDialog.setScene(scene);
			controller.setEditStage(editDialog);
			controller.setPromotion(promotion);
			controller.setPromotionsList(listPromotion);
			editDialog.setOnShown((event) -> {
	   			editDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - editDialog.getWidth() / 2); 
	   			editDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - editDialog.getHeight() / 2);
			});
   			editDialog.showAndWait();
		}
		catch(IOException e)
		{
			Main.showExceptionDialog(e);
		}
	}

	/*-----------------------------------------------Gestion des Modules Stages------------------------------------------------>*/
	
	public void createGMStage(Promotion promotion) 
   	{
		content.setScreen(gmScreen);
		
   	}
	
   	public void showGMAddDialog()
   	{
   		Stage addDialog = new Stage();
   		addDialog.initModality(Modality.APPLICATION_MODAL);
   		addDialog.setResizable(false);
   		addDialog.initOwner(primaryStage);
   		addDialog.setTitle("Gestion des Modules - Ajouter un Module");
   		addDialog.getIcons().add(new Image("Images/add.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GP/GM/GMAddView.fxml"));
   		try {
   			AnchorPane layoutPane =  loader.load();
   			Scene scene = new Scene(layoutPane);
   			addDialog.setScene(scene);
   			GMAddController controller = loader.getController();
   			controller.setPromotion(activePromotion);
   			controller.setAddStage(addDialog);
   			addDialog.setOnShown((event) -> {
   	   			addDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - addDialog.getWidth() / 2); 
   	   			addDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - addDialog.getHeight() / 2);
   			});
   			addDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   	}
   	
   	public void showGMEditDialog(Module module)
   	{
   		Stage editDialog = new Stage();
   		editDialog.initModality(Modality.APPLICATION_MODAL);
   	    editDialog.setResizable(false);
   		editDialog.initOwner(primaryStage);
   		editDialog.setTitle("Gestion des Module - Editer un Module");
   		editDialog.getIcons().add(new Image("Images/edit.png"));
   		FXMLLoader loader = new FXMLLoader();
   		loader.setLocation(Main.class.getResource("View/GP/GM/GMEditView.fxml"));
   		try {
   			AnchorPane layoutPane =  loader.load();
   			Scene scene = new Scene(layoutPane);
   			editDialog.setScene(scene);
   			GMEditController controller = loader.getController();
   			controller.setEditStage(editDialog);
   			controller.setPromotion(activePromotion);
   			controller.setModule(module);
   			editDialog.setOnShown((event) -> {
   	   			editDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - editDialog.getWidth() / 2); 
   	   			editDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - editDialog.getHeight() / 2);
   			});
   			editDialog.showAndWait();
   		}
   		catch(IOException e)
   		{
   			Main.showExceptionDialog(e);
   		}
   	}

	
	// GPL Stages
	
	public void createCreateGPLStage()
	{
		content.setScreen(gplCreateScreen);
	}
	
	public void createPlanningView()
	{
		content.setScreen(gplPlanningScreen);
		primaryStage.setMaximized(true);
	}
	
	public void createPlanningEditView()
	{
		ObservableList<Promotion> listPromotion;
		ChoiceDialog<Promotion> promotionChooser;
		try {
			listPromotion = FXCollections.observableArrayList();
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery("select idPromotion from planning");
			while(result.next())
			{
				PreparedStatement ps = connection.prepareStatement("select * from promotion where idPromotion = ?");
				ps.setString(1,result.getString("idPromotion"));
				ResultSet promotions = ps.executeQuery();
				while(promotions.next())
				{
					Promotion p = new Promotion();
					p.setIdentifiant(promotions.getString("idPromotion"));
					p.setNbSec(promotions.getInt("nbSections"));
					p.setNbGroupes(promotions.getInt("nbGroupes"));
					p.setModulesList(Module.consulter(p,null));
					listPromotion.add(p);
				}
			}
			promotionChooser = new ChoiceDialog<>(listPromotion.get(0),listPromotion);
			promotionChooser.setTitle("Modifier un planning - choisir une promotion");
			promotionChooser.setHeaderText("Choisissez la promotion à modifier son planning");
			promotionChooser.getDialogPane().getStylesheets().add("alert.css");
			promotionChooser.initModality(Modality.APPLICATION_MODAL);
			promotionChooser.initOwner(this.getPrimaryStage());
		    BorderPane sceneContent = (BorderPane) this.getPrimaryStage().getScene().getRoot();
		    StackPane parent = (StackPane) sceneContent.getCenter();
		    Node sContent = parent.getChildren().get(1);
		    sContent.toBack();
		    promotionChooser.showAndWait();
		    sContent.toFront();
		    if(promotionChooser.getResult() != null)
		    {
		    	ObservableList<Planning> plannings = Planning.consulter(promotionChooser.getResult());
		    	this.setActivePlanning(plannings.get(0));
		    	content.setScreen(gplEditPlanningScreen);
		    	primaryStage.setMaximized(true);
		    }
		}
		catch(Exception e)
		{
			Main.showExceptionDialog(e);
		}
	}
	
	public void showPDFGenDialog(Planning planning)
	{
		Stage genStage = new Stage();
		genStage.setTitle("Génerer un fichier PDF");
		genStage.initOwner(primaryStage);
		genStage.setResizable(false);
		genStage.initModality(Modality.APPLICATION_MODAL);
		genStage.setOnShown((event) -> {
   	   		genStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - genStage.getWidth() / 2); 
   	   		genStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - genStage.getHeight() / 2);
   		});
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("View/GPL/GenPDFView.fxml"));
		try {
			AnchorPane content = loader.load();
			Scene scene = new Scene(content);
			genStage.setScene(scene);
			GenPDFViewController controller = loader.getController();
			controller.setGenStage(genStage);
			controller.setPalnning(planning);
	        BorderPane sceneContent = (BorderPane) primaryStage.getScene().getRoot();
	        StackPane parent = (StackPane) sceneContent.getCenter();
	        Node sContent = parent.getChildren().get(1);
	        sContent.toBack();
	        genStage.showAndWait();
	        sContent.toFront();
		}
		catch(IOException e)
		{
			Main.showExceptionDialog(e);
		}
	}
   	
   	public Stage getPrimaryStage()
   	{
   		return primaryStage;
   	}

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<Local> getListLocal() {
		return listLocal;
	}
	
	public ObservableList<Enseignant> getListEnseignant() {
		return listEnseignant;
	}

	public ObservableList<Creneau> getListCreneau() {
		return listCreneau;
	}

	public ObservableList<Promotion> getListPromotion() {
		return listPromotion;
	}

	public Promotion getActivePromotion() {
		return activePromotion;
	}


	public void setActivePromotion(Promotion activePromotion) {
		this.activePromotion.setIdentifiant(activePromotion.getIdentifiant());
		this.activePromotion.setNbGroupes(activePromotion.getNbGroupes());
		this.activePromotion.setNbSec(activePromotion.getNbSec());
		this.activePromotion.getModulesList().setAll(activePromotion.getModulesList());
	}


	public Planning getActivePlanning() {
		return activePlanning;
	}


	public void setActivePlanning(Planning activePlanning) {
		this.activePlanning.setDateDebut(activePlanning.getDateDebut());
		this.activePlanning.setDateFin(activePlanning.getDateFin());
		this.activePlanning.setPromotion(activePlanning.getPromotion());
		this.activePlanning.setListExamen(activePlanning.getListExamen());
		this.activePlanning.setSemestre(activePlanning.getSemestre());
	}
	
	public static void showExceptionDialog(Exception ex)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add("alert.css");
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("Erreur technique");
		alert.setContentText(ex.getMessage());

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

}
