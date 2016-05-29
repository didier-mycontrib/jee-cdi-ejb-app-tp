package tp.myapp.web.mbean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import tp.myapp.ejb.itf.IConvertisseur;
@Named
//@ManagedBean
@SessionScoped //javax.context (CDI/JSR299/JEE6) plutot que javax.faces.bean (JSF2 only)
public class ConvertisseurMbean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Double montant;
	private Double sommeConvertie;
	
	//@Inject //avec CDI , beans.xml present dans WEB-INF
	@EJB
	private IConvertisseur serviceConvertisseur;
	
	public String convertir(){
		sommeConvertie=serviceConvertisseur.euroToFranc(montant);
		return null;//rester sur meme page
	}
	
	
	public Double getMontant() {
		return montant;
	}
	public void setMontant(Double montant) {
		this.montant = montant;
	}
	public Double getSommeConvertie() {
		return sommeConvertie;
	}
	public void setSommeConvertie(Double sommeConvertie) {
		this.sommeConvertie = sommeConvertie;
	}
	

}
