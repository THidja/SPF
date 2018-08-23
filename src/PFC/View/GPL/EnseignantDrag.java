package PFC.View.GPL;

import PFC.Model.Enseignant;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;

public class EnseignantDrag extends Label {
	
	
	private Enseignant enseignant;
	
	EnseignantDrag(Enseignant enseignant)
	{
		super(enseignant.toString());
		this.enseignant = enseignant;
		this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
		this.setTextAlignment(TextAlignment.CENTER);
		this.setPrefHeight(27);
		
		this.setOnDragDetected(event -> {
			Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
		    content.putString(this.getText());
		    db.setContent(content);
		    event.consume();
		});
		
		this.setOnDragDropped(event -> {
			
			if(event.getGestureSource() instanceof EnseignantDrop)
			{
				EnseignantDrop child = (EnseignantDrop) event.getGestureSource();
				Enseignant tmpEnseignant = child.getEnseignant();
				child.setEnseignant(this.getEnseignant());
				this.setEnseignant(tmpEnseignant);
				event.consume();
			}
			
		});		
	}

	public Enseignant getEnseignant() {
		return enseignant;
	}

	public void setEnseignant(Enseignant enseignant) {
		this.enseignant = enseignant;
		this.setText(enseignant.getNom());
	}
	
}
