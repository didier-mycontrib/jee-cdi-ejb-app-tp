package tp.myapp.ejb.itf;

import javax.ejb.Local;
import javax.ejb.Remote;

//@Local
@Remote
public interface IConvertisseur {
	
	public double euroToFranc(double montant);
	//...

}
