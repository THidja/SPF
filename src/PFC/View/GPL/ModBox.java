package PFC.View.GPL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import PFC.Model.Module;

public class ModBox extends VBox {
	
	private ObservableList<Module> data = FXCollections.observableArrayList();
	Pane parent;
	
	ModBox()
	{
		super();
		initialize();
	}
	
	ModBox(int spacing)
	{
		super(spacing);
		initialize();
	}
	
	private void initialize()
	{
		this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		this.setAlignment(Pos.TOP_LEFT);
		
		this.setOnDragEntered(event -> 
		{
			if(event.getGestureSource() instanceof ModuleDrop)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});
		this.setOnDragOver(event -> {
			if(event.getGestureSource() instanceof ModuleDrop)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});
		this.setOnDragDropped((dragEvent) -> {
			
			    boolean succes = false;
				Dragboard db = dragEvent.getDragboard();
				if(db.hasString())
				{
					if(dragEvent.getGestureSource() instanceof ModuleDrop)
					{
						ModuleDrop source = (ModuleDrop) dragEvent.getGestureSource();
						ModuleDrag se = new ModuleDrag(source.getExamen().getModule());
						this.getData().add(source.getExamen().getModule());
						this.getChildren().add(se);
						source.setText(null);
						source.getExamen().setModule(new Module());
						succes = true;
					}
					else 
					dragEvent.setDropCompleted(succes);
					dragEvent.consume();
				}
		});
		
	}
	
	public void setData(ObservableList<Module> data)
	{
		this.data = data;
		this.getChildren().clear();
		
		for(Module m : data)
		{
			ModuleDrag md = new ModuleDrag(m);
			getChildren().add(md);
		}
	}
	public void setParent(Pane parent)
	{
		this.parent = parent;
	}
	public void showOnly(ObservableList<Module> data)
	{
		this.getChildren().clear();
		
		for(Module m : data)
		{
			ModuleDrag md = new ModuleDrag(m);
			getChildren().add(md);
		}
	}
	public ObservableList<Module> getData()
	{
		return data;
	}
}
