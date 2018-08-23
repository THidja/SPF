
package PFC.View.GPL;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import PFC.Model.Enseignant;
import PFC.Model.Surveiller;

public class EnsDropLigne extends HBox {
	
	private boolean active = false; // if the Area of EnsDropLigne is the active area
	private Surveiller survaillance;
	
	EnsDropLigne()
	{
		init();
	}
	
	private void init()
	{
		this.setMaxWidth(Double.MAX_VALUE);
		this.setPrefHeight(27);
		this.setAlignment(Pos.CENTER);
		this.setSpacing(2);
		
		this.setOnDragEntered(event -> {
			
			if(event.getGestureSource() instanceof EnseignantDrag  && active)
			{
				Label l = (Label) event.getGestureSource();
				
				if(l.getText() != null)
				{
					event.acceptTransferModes(TransferMode.MOVE);
					event.consume();
					this.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,0.5),CornerRadii.EMPTY,null)));
				}
			}
			else
			{
				if(event.getGestureSource() instanceof EnseignantDrop && active)
				{
					EnseignantDrop child = (EnseignantDrop) event.getGestureSource();
					EnsDropLigne parent = (EnsDropLigne) child.getParent();
					if(parent.isActive())
					{
						event.acceptTransferModes(TransferMode.MOVE);
						event.consume();
						this.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,0.5),CornerRadii.EMPTY,null)));
					}
				}
			}
		});
		
		this.setOnDragExited(event -> {
			this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,CornerRadii.EMPTY,null)));
			ObservableList<Node> childs = this.getChildren();
			for(Node child : childs)
			{
				Label ens = (Label) child;
				ens.setTextFill(Color.BLACK);
			}
		});
		
		this.setOnDragOver(event -> {
			
			if(event.getGestureSource() instanceof EnseignantDrag && active)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				ObservableList<Node> childs = this.getChildren();
				for(Node child : childs)
				{
					Label ens = (Label) child;
					ens.setTextFill(Color.WHITE);
				}
				event.consume();
			}
			else
			{
				if(event.getGestureSource() instanceof EnseignantDrop && active)
				{
					EnseignantDrop child = (EnseignantDrop) event.getGestureSource();
					EnsDropLigne parent = (EnsDropLigne) child.getParent();
					if(parent.isActive())
					{
						event.acceptTransferModes(TransferMode.MOVE);
						ObservableList<Node> childs = this.getChildren();
						for(Node node : childs)
						{
							Label ens = (Label) node;
							ens.setTextFill(Color.WHITE);
						}
						event.consume();
						this.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,0.5),CornerRadii.EMPTY,null)));
					}
				}
			}
			
		});
		
		this.setOnDragDropped((event) ->
		{
			boolean succes = false;
			if(event.getGestureSource() instanceof EnseignantDrag && active)
			{
				EnseignantDrag element = (EnseignantDrag) event.getGestureSource();
		    	this.addElement(element);
				Node child = (Node) event.getGestureSource();
				Pane parent = (Pane) child.getParent();
				parent.getChildren().remove(child);
				SurvaillantsPane sp = (SurvaillantsPane) this.getParent();
				sp.getData().remove(element.getEnseignant());			
				succes = true;
			}
			else
			{
				if(event.getGestureSource() instanceof EnseignantDrop)
				{
					EnseignantDrop child = (EnseignantDrop) event.getGestureSource();
					EnsDropLigne parent = (EnsDropLigne) child.getParent();
					if(parent.isActive())
					{
						this.addElement(child);
						succes = true;
					}
				}
			}
			event.setDropCompleted(succes);
			event.consume();
		});
	}
	
	public void addElement(EnseignantDrag e)
	{
		EnseignantDrop element = new EnseignantDrop(e.getEnseignant()); 
		this.getSurvaillance().getSurveillants().add(e.getEnseignant());
		this.getChildren().add(element);
	}
	public void addElement(EnseignantDrop e)
	{
		EnsDropLigne parent = (EnsDropLigne) e.getParent();
		parent.getSurvaillance().getSurveillants().remove(e.getEnseignant());
		parent.getChildren().remove(e);
		this.getSurvaillance().getSurveillants().add(e.getEnseignant());
		this.getChildren().add(e);
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public Surveiller getSurvaillance()
	{
		return this.survaillance;
	}
	
	public void setSurvaillance(Surveiller survaillance)
	{
		this.survaillance = survaillance;
		for(Enseignant e : survaillance.getSurveillants())
		{
			EnseignantDrop ed = new EnseignantDrop(e);
			this.getChildren().add(ed);
		}
	}

}
