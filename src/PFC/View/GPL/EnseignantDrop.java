package PFC.View.GPL;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import PFC.Model.Enseignant;

public class EnseignantDrop extends Label {
	
	private Enseignant enseignant;
	
	EnseignantDrop(Enseignant enseignant)
	{
		super(enseignant.getNom());
		this.enseignant = enseignant;
		this.setPadding(new Insets(0,0,0,5));
		this.setFont(new Font(8));
		
		this.setOnDragDetected(event -> {
			EnsDropLigne parent = (EnsDropLigne) this.getParent();
			if(parent.isActive()) // if active autorise to Drag this Element
			{
				Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
			    content.putString(this.getText());
			    db.setContent(content);
			    event.consume();
			}
		});
		
		this.setOnDragEntered(event -> {
			if(event.getGestureSource() instanceof EnseignantDrop)  // to autorise permutation
			{
				EnsDropLigne parent = (EnsDropLigne) this.getParent();
				if(parent.isActive())
				{
					Dragboard db = event.getDragboard();
					if(db.hasString())
					{
						event.acceptTransferModes(TransferMode.MOVE);
						event.consume();
					}
				}
			}
		});
		
		this.setOnDragOver(event ->  {
			
			if(event.getGestureSource() instanceof EnseignantDrop) // to autorise permutation
			{
				EnsDropLigne parent = (EnsDropLigne) this.getParent();
				if(parent.isActive())
				{
					event.acceptTransferModes(TransferMode.MOVE);
					event.consume();
				}
			}
		});
		
		this.setOnDragDropped(event -> {
			
			if(event.getGestureSource() instanceof EnseignantDrop) // do permutation
			{
				EnsDropLigne parent = (EnsDropLigne) this.getParent();
				if(parent.isActive())
				{
					event.acceptTransferModes(TransferMode.MOVE);
					EnseignantDrop source = (EnseignantDrop) event.getGestureSource();
					EnsDropLigne sourceParent = (EnsDropLigne) source.getParent(); 
					parent.getSurvaillance().getSurveillants().remove(this.getEnseignant());
					parent.getSurvaillance().getSurveillants().add(source.getEnseignant());
					sourceParent.getSurvaillance().getSurveillants().remove(source.getEnseignant());
					sourceParent.getSurvaillance().getSurveillants().add(this.getEnseignant());
					Enseignant tmpEnseignant = this.getEnseignant();
					this.enseignant = source.getEnseignant();
					source.enseignant = tmpEnseignant;
					this.setText(this.getEnseignant().getNom());
					source.setText(source.getEnseignant().getNom());
					event.consume();
				}
			}
		});
	
	}

	public Enseignant getEnseignant() {
		return enseignant;
	}

	public void setEnseignant(Enseignant enseignant) {
		this.setText(enseignant.getNom());
		EnsDropLigne parent = (EnsDropLigne) this.getParent();
		SurvaillantsPane sp = (SurvaillantsPane) parent.getParent();
		sp.getData().remove(enseignant);
		sp.getData().add(this.getEnseignant());
		parent.getSurvaillance().getSurveillants().remove(this.enseignant);
		parent.getSurvaillance().getSurveillants().add(enseignant);
		this.enseignant = enseignant;
	}
	

}
