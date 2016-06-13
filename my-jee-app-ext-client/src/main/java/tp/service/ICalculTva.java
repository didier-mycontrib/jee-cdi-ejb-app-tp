package tp.service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ICalculTva {
     public double tva(@WebParam(name="ht")double ht,
    		           @WebParam(name="taux")double taux);
     
     public double ttc(@WebParam(name="ht")double ht,
	                   @WebParam(name="taux")double taux);
}
