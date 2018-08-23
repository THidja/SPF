package PFC.View.GPL;

import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;
import PFC.Model.Module;

public class ModuleDrag extends Label{
	
	
	private Module module;
	
	ModuleDrag(Module module) 
	{
		this.setModule(module);
		this.setText(module.getIntitule());
		init();
	}
	
	public void init()
	{
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
			
			if(event.getGestureSource() instanceof ModuleDrop)
			{
				event.acceptTransferModes(TransferMode.MOVE);
				ModuleDrop source = (ModuleDrop) event.getGestureSource();
				ModBox parent = (ModBox) this.getParent();
				parent.getData().remove(this.getModule());
				parent.getData().add(source.getExamen().getModule());
				source.setText(this.getModule().planningFormat());
				this.setText(source.getExamen().getModule().getIntitule());
				Module tmpModule = this.getModule();
				this.setModule(source.getExamen().getModule());
				source.getExamen().setModule(tmpModule);
				event.consume();
			}
		});
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

}
