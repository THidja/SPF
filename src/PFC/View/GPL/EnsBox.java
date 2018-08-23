package PFC.View.GPL;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import PFC.Model.Enseignant;

public class EnsBox extends VBox {
	
	private ObservableList<Enseignant> data;
	
	EnsBox()
	{
		super();
		initialize();
	}
	
	EnsBox(int spacing)
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
			if(event.getGestureSource() instanceof EnseignantDrop)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});
		this.setOnDragOver(event -> {
			if(event.getGestureSource() instanceof EnseignantDrop )
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
					if(dragEvent.getGestureSource() instanceof EnseignantDrop)
					{
						EnseignantDrop source = (EnseignantDrop) dragEvent.getGestureSource();
						EnsDropLigne parent = (EnsDropLigne) source.getParent();
						SurvaillantsPane sp = (SurvaillantsPane) parent.getParent();
						sp.getData().add(source.getEnseignant());
						EnseignantDrag se = new EnseignantDrag(source.getEnseignant());
						this.getChildren().add(se);
						parent.getSurvaillance().getSurveillants().remove(source.getEnseignant());
						parent.getChildren().remove(source);
						succes = true;
					}
				}
					dragEvent.setDropCompleted(succes);
					dragEvent.consume();
		});
		
		this.setOnDragOver((event) -> 
		{
			event.acceptTransferModes(TransferMode.MOVE);
			event.consume();
		});
	}
	
	public void setData(ObservableList<Enseignant> data)
	{
		this.data = data;
		this.getChildren().clear();
		
		for(Enseignant e : data)
		{
			getChildren().add(new EnseignantDrag(e));
		}
	}
	
	public void showOnly(ObservableList<Enseignant> data)
	{
		this.getChildren().clear();
		
		for(Enseignant e : data)
		{
			getChildren().add(new EnseignantDrag(e));
		}
	}
	
	public ObservableList<Enseignant> getData()
	{
		return data;
	}
}
