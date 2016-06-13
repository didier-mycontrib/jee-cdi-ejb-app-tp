package myapp.jee.client;

import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import tp.myapp.ejb.itf.IConvertisseur;

public class MyJeeClientApp {
	
	private static final String JBOSS_WILDFLY="JbossWildFly";
	private static final String JBOSS_AS_7_1="Jboss_AS_7_1";
	private static final String GLASSFISH="Glassfish";
	//private static String serverType= JBOSS_WILDFLY;
	private static String serverType= JBOSS_AS_7_1;
	//private static String serverType= GLASSFISH;

	
	private static ConnectionFactory connectionFactory=null;

	private static Queue queue;
	
	private static Properties props = new Properties();
	
	public static void initJndiClientProperties() {
		if(serverType.equals(GLASSFISH)){
		//pour glassfish (v3,v4) sur localhost :
			props.put("java.naming.factory.initial","com.sun.enterprise.naming.SerialInitContextFactory");
			props.put("java.naming.factory.url.pkgs","com.sun.enterprise.naming");
			props.put("java.naming.factory.state","com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			props.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
			props.put("org.omg.CORBA.ORBInitialPort", "3700");
		}
		else if(serverType.equals(JBOSS_WILDFLY)){
			props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080"); //remote://localhost:4447 for Jboss7.1 , http-remoting://localhost:8080 for wildfly 8,9
			props.put(Context.SECURITY_PRINCIPAL, "guest");//"admin" , "guest" , "..."
			props.put(Context.SECURITY_CREDENTIALS, "guest007"); //"pwd", "guest007"
			//avec admin = utilisateur ajoute via la commande JBOSS7_HOME/bin/add-user 
			//mot de passe=pwd et roles associos admin,guest
			//et avec "guest" = role configure sur la partie "messaging" de standalone(-full).xml 
			props.put("jboss.naming.client.ejb.context", true);
		}
		else if(serverType.equals(JBOSS_AS_7_1)){
			
			props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			props.put(Context.PROVIDER_URL, "remote://localhost:4447"); //remote://localhost:4447 for Jboss7.1 , http-remoting://localhost:8080 for wildfly 8,9
			props.put(Context.SECURITY_PRINCIPAL, "guest");//"admin" , "guest" , "..."
			props.put(Context.SECURITY_CREDENTIALS, "guest007"); //"pwd", "guest007"
			//avec admin = utilisateur ajoute via la commande JBOSS7_HOME/bin/add-user 
			//mot de passe=pwd et roles associes admin,guest
			//et avec "guest" = role configure sur la partie "messaging" de standalone(-full).xml 
			props.put("jboss.naming.client.ejb.context", true);
			
			
			//props.setProperty(Context.URL_PKG_PREFIXES,"org.jboss.ejb.client.naming");

		}
	}

	public static void main(String[] args) {
		initJndiClientProperties();
		try {
			Context jndiContext = new InitialContext(props);
			//testJms(jndiContext);
			Thread.sleep(2000);
			testConversionSurEjbRemote(jndiContext);
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	public static void testConversionSurEjbRemote(Context jndiContext) {
		try {
			IConvertisseur ejbConvertisseur = null;
			if(serverType.equals(GLASSFISH)){
				ejbConvertisseur = (IConvertisseur) jndiContext.lookup("tp.myapp.ejb.itf.IConvertisseur");
		         //NB: if not set , username/password will be prompted by AAC / appclient in order to access secure remote EJB
			}else if(serverType.equals(JBOSS_WILDFLY)){
				ejbConvertisseur = (IConvertisseur) jndiContext.lookup("my-jee-app/my-jee-app-ejb-impl/ConvertisseurBean!tp.myapp.ejb.itf.IConvertisseur");
				//app/minibank-jee6-ejb-1.0-SNAPSHOT/GestionComptesImpl!tp.myapp.minibank.core.impl.ejb.itf.GestionComptesRemote
			}else if(serverType.equals(JBOSS_AS_7_1)){
				//String jndiName = "ejb:my-jee-app/my-jee-app-ejb-impl/ConvertisseurBean!tp.myapp.ejb.itf.IConvertisseur"; //pour version avec props.setProperty(Context.URL_PKG_PREFIXES,"org.jboss.ejb.client.naming"); et fichier jboss-ejb-client.properties dans src/main/resources 
				String jndiName = "my-jee-app/my-jee-app-ejb-impl/ConvertisseurBean!tp.myapp.ejb.itf.IConvertisseur"; //pour version sans avec props.setProperty(Context.URL_PKG_PREFIXES,"org.jboss.ejb.client.naming"); 
				ejbConvertisseur = (IConvertisseur) jndiContext.lookup(jndiName);
				// minibank-jee6-ejb-1.0-SNAPSHOT/GestionComptesImpl!tp.myapp.minibank.core.impl.ejb.itf.GestionComptesRemote
			}
	
			
			System.out.println("test_convertir sur ejb remote:");
	        System.out.println("30 euros= "  + ejbConvertisseur.euroToFranc(30) + " francs");
	       
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static Connection initJmsConnection(Context jndiContext){
		Connection connection = null;
		try {
			if(serverType.equals(GLASSFISH)){
			    connectionFactory = (ConnectionFactory)jndiContext.lookup("jms/__defaultConnectionFactory");
			    //predefini dans glassfish
			    connection = connectionFactory.createConnection();
			}
			else if(serverType.equals(JBOSS_WILDFLY)){
				 connectionFactory = (ConnectionFactory)jndiContext.lookup("jms/RemoteConnectionFactory");
				//avec <entry name="java:jboss/exported/jms/RemoteConnectionFactory"/>           
				//dans standalone(-full).xml 
				 connection = connectionFactory.createConnection( props.getProperty(Context.SECURITY_PRINCIPAL), props.getProperty(Context.SECURITY_CREDENTIALS)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	
	public static void testJms(Context jndiContext) {
		try {
			Connection connection = initJmsConnection(jndiContext);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			//queue = (Queue) jndiContext.lookup("myQueue"); //or jms/myQueue ?
			//Queue queue = (Queue) jndiContext.lookup("jms/queue/myQueue"); //pour jboss_as_7_1
			// avec queue/myQueue qui doit etre exporte dans standalone(-full).xml 
			//<entry name="java:jboss/exported/jms/queue/myQueue"/> 
			
			queue=session.createQueue("myQueue"); //NB: createQueue() create a new queue or open an existing one
			
			MessageProducer messageProducer = session.createProducer(queue);
			TextMessage message = session.createTextMessage();
			
			final int NUM_MSGS=3;
			for (int i = 0; i < NUM_MSGS; i++) {
			    message.setText("This is message " + (i + 1));
			    System.out.println("Sending message: " + message.getText());
			    messageProducer.send(message);
			}
			messageProducer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	
}
