package PFC.View.GPL;


import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import PFC.Model.Examen;
import PFC.Model.Module;

public class ModuleDrop extends Label {
	
	private boolean succes = false;
	private Examen examen;
	
	ModuleDrop()
	{
		super();
		init();
	}
	ModuleDrop(Module m)
	{
		this();
		this.setText(m.planningFormat());
		succes = true;
	}
	
	private void init()
	{
		this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		this.setTextAlignment(TextAlignment.CENTER);
		this.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY,CornerRadii.EMPTY,new Insets(2))));
		
		this.textProperty().addListener((observable,oldValue,newValue) -> {
			
			if(newValue == null)
			{
				this.setGraphic(new ImageView("Images/DropLogo.png"));
				this.setContentDisplay(ContentDisplay.CENTER);
				this.setOpacity(0.1);
				succes = false;
			}
			else
			{
				this.setOpacity(1);
				this.setGraphic(null);
			}
			
		});
		
		this.setText(null);
				
		this.setOnDragDetected(event -> {
			
			Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
		    content.putString(this.getText());
		    db.setContent(content);
		    event.consume();
		    
		});
				
		this.setOnDragEntered(event -> {
			
			if(event.getGestureSource() instanceof ModuleDrag || event.getGestureSource() instanceof ModuleDrop)
			{
				Label l = (Label) event.getGestureSource();
				
				if(l.getText() != null)
				{
					this.setOpacity(0.5);
					event.acceptTransferModes(TransferMode.MOVE);
					event.consume();
				}
			}
		});
		
		this.setOnDragOver(event -> {
			
			if(event.getGestureSource() instanceof ModuleDrag || event.getGestureSource() instanceof ModuleDrop)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}			
		});
		
		this.setOnDragExited(event -> {
			
			if(succes)
			{
				this.setOpacity(1);
			}
			else
			{
				this.setOpacity(0.1);
			}
			event.acceptTransferModes(TransferMode.NONE);
			event.consume();
		});
		
		this.setOnDragDropped((event) ->
		{
			Dragboard db = event.getDragboard();
			
			if(db.hasString())
			{
				if(event.getGestureSource() instanceof ModuleDrop && event.getGestureTarget() instanceof ModuleDrop)
				{
					ModuleDrop e1 = (ModuleDrop) event.getGestureSource();
					ModuleDrop e2 = (ModuleDrop) event.getGestureTarget();
					
					if(e1.getText() != null && e2.getText() != null)
					{
						e1.setText(e2.getText());
						e2.setText(db.getString());
						Module tmpModule = e1.getExamen().getModule();
						e1.getExamen().setModule(e2.getExamen().getModule());
						e2.getExamen().setModule(tmpModule);
						succes = true;
					}
					else 
					{
						if(e2.getText() == null)
						{
							e2.setText(db.getString());
							e1.setText(null);
							e2.getExamen().setModule(e1.getExamen().getModule());
							e1.getExamen().setModule(null);
							succes = true;
						}
					}
				}
				else
				{
					if(event.getGestureSource() instanceof ModuleDrag && event.getGestureTarget() instanceof ModuleDrop	)
					{
						if(this.getText() == null)
						{
							ModuleDrag child = (ModuleDrag) event.getGestureSource();
							this.setText(child.getModule().planningFormat());
							this.getExamen().setModule(child.getModule());
							ModBox parent = (ModBox) child.getParent();
							parent.getChildren().remove(child);
							parent.getData().remove(child.getModule());
							succes = true;
						}
						else 
						{
							ModuleDrag source = (ModuleDrag) event.getGestureSource();
							ModBox parent = (ModBox) source.getParent();
							parent.getData().add(this.getExamen().getModule());
							parent.getData().remove(source.getModule());
							Module tmpModule =  source.getModule();
							source.setText(this.getExamen().getModule().getIntitule());
							source.setModule(this.getExamen().getModule());
							this.setText(tmpModule.planningFormat());
							this.getExamen().setModule(tmpModule);

							succes = true;
						}
					}
				}
			}
			event.setDropCompleted(succes);
			event.consume();
		});
	}
	
	public void setExamen(Examen examen)
	{
		this.examen = examen;
	}
	
	public Examen getExamen()
	{
		return this.examen;
	}
	

}
