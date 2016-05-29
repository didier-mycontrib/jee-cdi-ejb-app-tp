
package tp.myapp.ejb.test;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tp.myapp.ejb.itf.IConvertisseur;


public class TestServiceConvertisseurViaOpenEjb extends MyAbstractOpenEjbTest{ 

    @Inject
	private IConvertisseur service = null; // service metier a tester
	

	  @Before
	     public void initService(){
	       if(service==null){
	         try{
	          // String openEjbJndiName="ConvertisseurBean" + "Local";
	           String openEjbJndiName="ConvertisseurBean" + "Remote";
	           service= (IConvertisseur )                
	                context.lookup(openEjbJndiName);
			}catch(Exception ex){ex.printStackTrace();}
		}
	      }
	        

	@Test
   public void test_euroToFranc() {
     try{
        System.out.println("test_getDeviseByName");
        double sommeFrancs = service.euroToFranc(15);
        System.out.println("15 euros =  " + sommeFrancs + " francs");
        Assert.assertTrue(sommeFrancs > 98 && sommeFrancs < 99);
        }catch(Exception /*ServiceException*/ ex){
      	    System.err.println(ex.getMessage());
      	    Assert.fail(ex.getMessage());
      	}
   }
	
      
}
