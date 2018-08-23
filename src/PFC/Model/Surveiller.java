package PFC.Model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Surveiller {
	
	private Local local;
	private ObservableList<Enseignant> surveillants;
	private ObservableList<String> groupes;
	
	public Surveiller()
	{
		local = new Local();
		surveillants = FXCollections.observableArrayList();
		groupes = FXCollections.observableArrayList();
	}
	
	public Local getLocal() {
		return local;
	}
	public void setLocal(Local local) {
		this.local = local;
	}
	public ObservableList<Enseignant> getSurveillants() {
		return surveillants;
	}
	public void setSurveillants(ObservableList<Enseignant> surveillants) {
		this.surveillants = surveillants;
	}
	public ObservableList<String> getGroupes() {
		return groupes;
	}
	public void setGroupes(ObservableList<String> groupes) {
		this.groupes = groupes;
	}
	
	public String texFormatForGL()
	{
		String toRet;
		toRet = local.getIdentifiant()+" ";
		if(groupes.size() > 0)
		{
			toRet = toRet + "(" + groupes.get(0);
			for(int i=1;i<groupes.size();i++)
			{
				toRet = toRet + "+" + groupes.get(i);
			}
			toRet = toRet + ")";
		}
		return toRet;
	}
	public String texFormatForS()
	{
		String toRet = "";
		if(this.getSurveillants().size() > 0)
		{
			toRet = toRet + this.getSurveillants().get(0).getNom();
			for(int i=1;i<this.getSurveillants().size();i++)
			{
				toRet = toRet + " - " + this.getSurveillants().get(i).getNom();
			}
		}
		return toRet;
	}

}
