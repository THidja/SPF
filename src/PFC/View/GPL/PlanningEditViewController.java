package PFC.View.GPL;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.controlsfx.control.Notifications;

import PFC.ControlledScreen;
import PFC.Main;
import PFC.ScreensController;
import PFC.Model.BDDConnector;
import PFC.Model.Creneau;
import PFC.Model.Enseignant;
import PFC.Model.Examen;
import PFC.Model.Local;
import PFC.Model.Module;
import PFC.Model.Planning;
import PFC.Model.Surveiller;
import PFC.Util.DataOperations;
import PFC.Util.DateOperations;



public class PlanningEditViewController implements Initializable,ControlledScreen {
	
	private Main main;
	private Planning planning;
	private ScreensController myController;
	private SurvaillantsPane previousSP = null;
	
	@FXML
	private Label promotionLabel;
	@FXML
	private TextField searchField;
	@FXML
	private SplitPane contentSplit;
	@FXML
	private ScrollPane modSC;
	@FXML
	private AnchorPane modulesPane;
	@FXML
	private StackPane parent;
	@FXML
	private ScrollPane ensSC;
	@FXML
	private AnchorPane enseignantsPane;
	@FXML
	private GridPane planningArea;
    @FXML
    private ScrollPane scPane;
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane modPane;
    @FXML
    private TitledPane ensPane;
	@FXML
	private BorderPane board;
	@FXML
	private Button saveButton;
	@FXML
	private Button generateButton;
	@FXML
	private Button backButton;
	
	public void setMain(Main main)
	{
		this.main = main;
		this.planning = main.getActivePlanning();
	}

	@Override
	public void setScreenParent(ScreensController screenPage) {
		myController = screenPage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		planningArea.setAlignment(Pos.CENTER);
		saveButton.setGraphic(new ImageView("Images/save.png"));
		generateButton.setGraphic(new ImageView("Images/pdf.png"));
		backButton.setGraphic(new ImageView("Images/back-arrow.png"));
		SplitPane.setResizableWithParent(contentSplit, false);
	}
	
	@Override
	public void viewed() {
		
		planningArea.getChildren().clear();
		
		Label headerLabel = new Label("Date");
		headerLabel.getStyleClass().add("header-label");
		planningArea.addRow(0,headerLabel);
	
		headerLabel = new Label("Module");
		headerLabel.getStyleClass().add("header-label");
		planningArea.add(headerLabel,1,0);
		
		headerLabel = new Label("Lieu");
		headerLabel.getStyleClass().add("header-label");
		planningArea.add(headerLabel,2,0);
		
		headerLabel = new Label("Surveillants");
		headerLabel.getStyleClass().add("header-label");
		planningArea.add(headerLabel,3,0);
		
		promotionLabel.setText(planning.getPromotion().getIdentifiant());
		
		ObservableList<Creneau> listCreneau ;
		scPane.widthProperty().addListener((observable,oldValue,newValue) -> {
			planningArea.setPrefWidth(newValue.doubleValue());
		}); 
		
		try {
			
			// Modules Box
			ModBox modulesBox = new ModBox(4);
			modulesBox.setParent(parent);
			modulesPane.getChildren().clear();
			modulesPane.getChildren().add(modulesBox);
			AnchorPane.setLeftAnchor(modulesBox,0.0);
			AnchorPane.setRightAnchor(modulesBox,0.0);
			AnchorPane.setTopAnchor(modulesBox, 0.0);
			AnchorPane.setBottomAnchor(modulesBox,0.0);
			
			modSC.widthProperty().addListener((obs,oldValue,newValue) -> {
				modulesPane.setPrefWidth(newValue.doubleValue() - 2);
			});
			modSC.heightProperty().addListener((obs,oldValue,newValue) -> {
				modulesPane.setPrefHeight(newValue.doubleValue() - 2);
			});
			
			
			// Enseignants Box 
			EnsBox enseignantsBox = new EnsBox(4);
			enseignantsPane.getChildren().clear();
			enseignantsPane.getChildren().add(enseignantsBox);
			AnchorPane.setLeftAnchor(enseignantsBox,0.0);
			AnchorPane.setRightAnchor(enseignantsBox,0.0);
			AnchorPane.setTopAnchor(enseignantsBox, 0.0);
			AnchorPane.setBottomAnchor(enseignantsBox,0.0);
			
			ensSC.widthProperty().addListener((obs,oldValue,newValue) -> {
				enseignantsPane.setPrefWidth(newValue.doubleValue() - 2);
			});
			
			ensSC.heightProperty().addListener((obs,oldValue,newValue) -> {
				enseignantsPane.setPrefHeight(newValue.doubleValue() - 2);
			});
			
			searchField.textProperty().addListener((obs,oldValue,newValue) -> {
				if(newValue == null)
				{
					if(ensPane.isExpanded())
					{
						enseignantsBox.showOnly(enseignantsBox.getData());
					}
					else 
					{
						if(modPane.isExpanded())
						{
							modulesBox.showOnly(modulesBox.getData());
						}
					}
				}
				else 
				{
					if(ensPane.isExpanded())
					{
						ObservableList<Enseignant> data = FXCollections.observableArrayList();
						for(Enseignant e : enseignantsBox.getData())
						{
							if(e.getNom().toLowerCase().contains(newValue.toLowerCase()) ||
							   e.getPrenom().toLowerCase().contains(newValue.toLowerCase()) )
							{
								data.add(e);
							}
						}
						enseignantsBox.showOnly(data);
					}
					else
					{
						if(modPane.isExpanded())
						{
							ObservableList<Module> data = FXCollections.observableArrayList();
							for(Module m : modulesBox.getData())
							{
								if(m.getIntitule().toLowerCase().contains(newValue.toLowerCase()))
								{
									data.add(m);
								}
							}
							modulesBox.showOnly(data);
						}
					}
				}
			});
			
			
			// Planning Area
			
			listCreneau = Creneau.consulter(planning.getPromotion(),planning.getDateDebut(),planning.getDateFin());
			
			int row = 1;
			
			Label dayLabel;
			Label dateLabel;
			Label heureLabel;
			VBox dateGrid;
			VBox locauxGrid;
			HBox locauxLigne;
			ObservableList<Module> listModule = null;
			ObservableList<Module> minusModuleList = FXCollections.observableArrayList();
			ObservableList<Enseignant> listEnseignant = null;

			listModule = Module.consulter(planning.getPromotion(),planning.getSemestre());
			
			
			// 
			
			
			for(Creneau c : listCreneau)
			{
				
				final Examen examen = planning.getListExamen().get(row-1);
				
				 // Date Grid Data - Grid N°0
				
				 dayLabel = new Label(DateOperations.getDayOf(c.getDate().getDayOfWeek().getValue()));
				 dayLabel.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
				 dateLabel = new Label(c.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				 dateLabel.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
				 heureLabel = new Label(c.getheure().toString());
				 heureLabel.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
				 dateGrid = new VBox(2);
				 dateGrid.setAlignment(Pos.CENTER);
				 dateGrid.getChildren().add(dayLabel);
				 dateGrid.getChildren().add(dateLabel);
				 dateGrid.getChildren().add(heureLabel);
				 planningArea.addRow(row,dateGrid);
				 planningArea.getColumnConstraints().get(0).setMaxWidth(180);
				 
				 // Set Exam data
				 
				 examen.setDate(c.getDate());
				 examen.setHeure(c.getheure());
				 
				 // Modules Grid Data - Grid N°1
				 ModuleDrop moduleDrop;
				 if(examen.getModule().getIntitule() != null)
				 {
					 moduleDrop = new ModuleDrop(examen.getModule());
				 }
				 else 
				 {
					 moduleDrop = new ModuleDrop();
				 }
				 moduleDrop.setOnMouseClicked(mouseEvent -> {
					 accordion.setExpandedPane(modPane);
				 });
				 moduleDrop.setExamen(examen); // if module are droped that will be added to the examen Object 
				 planningArea.add(moduleDrop,1,row);
				 
				 // Surveillants Grid Data - Grid N°3
				 
				 // Get Free Teachers 
				 
				 listEnseignant = FXCollections.observableArrayList(); 
				 Connection connection = BDDConnector.getInstance();
				 PreparedStatement ps = connection.prepareStatement("select * from enseignant where nom not in "
				 		+ "(select nom from surveiller where date = ? and heure = ?) ");
				 ps.setDate(1,Date.valueOf(c.getDate()));
				 ps.setTime(2,Time.valueOf(c.getheure()));
				 ResultSet result = ps.executeQuery();
				 while(result.next())
				 {
					 Enseignant enseignant = new Enseignant();
					 enseignant.setNom(result.getString("nom"));
					 enseignant.setPrenom(result.getString("prenom"));
					 enseignant.setGrade(result.getString("grade"));
					 listEnseignant.add(enseignant);
				 }
				 
				 SurvaillantsPane sp = new SurvaillantsPane(listEnseignant);
				 
				 sp.setDate(c.getDate());
				 sp.setTime(c.getheure());
				 sp.setOnMouseClicked(mouseEvent -> {
					 enseignantsBox.setData(sp.getData());
					 enseignantsPane.getChildren().clear();
					 enseignantsPane.getChildren().add(enseignantsBox);
					 
					 if(previousSP != null)
					 {
						 for(Node node : previousSP.getChildren())
						 {
							 EnsDropLigne edl = (EnsDropLigne) node;
							 edl.setActive(false);
						 }
						 
						 previousSP.setBackground(null);
					 }
					 previousSP = sp;
					 sp.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,0.1),CornerRadii.EMPTY,null)));
					 for(Node node : sp.getChildren())
					 {
						 EnsDropLigne edl = (EnsDropLigne) node;
						 edl.setActive(true);
					 }
					 ensPane.setExpanded(true);
				 });
				 
				 planningArea.add(sp,3,row);
				 
				 // Locaux Grid Data - Grid N°2
				 
				 locauxGrid = new VBox(0);
				 locauxGrid.setAlignment(Pos.TOP_CENTER);
				 
				 final ObservableList<String> groupesList = FXCollections.observableArrayList();
				 
				 // get promotions groups
				 
				 for(int i=1;i<=planning.getPromotion().getNbSec();i++)
				 {
					for(int j=1;j<=planning.getPromotion().getNbGroupes();j++)
					{
						groupesList.add(DataOperations.getLatinAlphabetLettreOf(i)+""+j);
					}
				 }
				 
				 int i = 0;
				 
				 for(Local l : c.getLocalList())
				 {					
					 final Surveiller survaillance = examen.getSurvaillances().get(i);
					 EnsDropLigne edl = new EnsDropLigne();
					 edl.setSurvaillance(survaillance);
					 sp.getChildren().add(edl);
					 //	 
					 locauxLigne = new HBox(2);
					 locauxLigne.setAlignment(Pos.CENTER);
					 
					 final Button ggcButton;
					 final Label localLabel = new Label(l.toString());
					 final Label groupesLabel = new Label();
					 
					 localLabel.setPrefHeight(27);
					 localLabel.setPadding(new Insets(0,0,0,5));
					 
					 ggcButton = new Button("choisir les groupes");
					 
					 ggcButton.setStyle("-fx-background-color:black;-fx-background-insets:2;");
					 ggcButton.setOpacity(0.4);
					 
					 ggcButton.setOnMouseEntered(event -> {
						 ggcButton.setOpacity(1);
					 });
					 
					 ggcButton.setOnMouseExited(event -> {
						 ggcButton.setOpacity(0.4);
					 });
					 
					 final ObservableList<String> checkedState = FXCollections.observableArrayList();
					 final GPLGroupeChooser ggc = new GPLGroupeChooser();
					 ggc.initParent(main.getPrimaryStage());
					 
					 groupesList.removeAll(survaillance.getGroupes());
					 checkedState.setAll(survaillance.getGroupes());
					 groupesLabel.setText(DataOperations.groupesOf(checkedState));
					 
					 ggcButton.setOnAction(event -> {
						 
					     groupesList.addAll(checkedState);
					     ggc.setGroupes(groupesList);
						 ggc.checkElements(checkedState);
						 ggc.showAndWait();
						 checkedState.setAll(ggc.getCheckedElements());
						 if(ggc.getData() != null)
						 {
							 groupesLabel.setText(" "+ggc.getData()); 
						 }
						 else
						 {
							 groupesLabel.setText("");
						 }
						 groupesList.removeAll(checkedState);
						 //
						 survaillance.setGroupes(checkedState);
					 });
					 
					 locauxLigne.getChildren().add(localLabel);
					 locauxLigne.getChildren().add(groupesLabel);
					 locauxLigne.getChildren().add(ggcButton);
					 locauxGrid.setAlignment(Pos.CENTER_LEFT);
					 locauxGrid.getChildren().add(locauxLigne);
					 
					 i++;
				 }
				 planningArea.add(locauxGrid,2,row);
				 
				 minusModuleList.add(examen.getModule());
				 
				 row++;
			}
			
			planningArea.setGridLinesVisible(false);
			planningArea.setGridLinesVisible(true);
			
			// set ModulesBox Data 
			listModule.removeAll(minusModuleList);
			modulesBox.setData(listModule);
			
		}
		catch(Exception e)
		{
			Main.showExceptionDialog(e);
		}
	}
	
	@FXML
	private void saveHandler()
	{
		try {
			Planning.supprimer(planning);
			Planning.ajouter(planning);
			Notifications.create()
						 .title("Sauvergarde du planning")
						 .text("le planning a bien etait enregistrer")
						 .position(Pos.BOTTOM_LEFT)
						 .darkStyle()
						 .show();
						 
		} catch (SQLException e) {
			Notifications.create()
						 .title("Erreur de sauvegarde")
						 .text(e.getMessage())
						 .darkStyle()
						 .position(Pos.BOTTOM_LEFT)
						 .showError();
			Main.showExceptionDialog(e);
		}
	}
	@FXML
	private void generateHandler()
	{
		this.saveHandler();
		main.showPDFGenDialog(planning);
	}
	@FXML
	private void backButtonHandler()
	{
		myController.setScreen(Main.mainScreen);
		main.getPrimaryStage().setMaximized(false);
	}
	@FXML
	private void keysHandler(KeyEvent event)
	{
		if(event.isControlDown() && event.getCode() == KeyCode.S)
		{
			this.saveHandler();
		}
		else if (event.getCode() == KeyCode.BACK_SPACE)
		{
			this.backButtonHandler();
		}
	}
    @FXML
    private void fsHandler()
    {
    	main.getPrimaryStage().setFullScreen(!main.getPrimaryStage().isFullScreen());
    }
}
