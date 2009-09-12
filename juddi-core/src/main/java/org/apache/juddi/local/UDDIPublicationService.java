package org.apache.juddi.local;

import java.util.HashMap;
import java.util.TreeSet;

import org.apache.juddi.Registry;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class UDDIPublicationService {

	  // collection of valid operations


	private HashMap<String, String> operations = null;

	
	  public UDDIPublicationService() {
		super();
		operations = new HashMap<String, String>();
		operations.put("get_registeredInfo", "getRegisteredInfo");
	  	operations.put("save_business", "saveBusiness");
	  	operations.put("save_service", "saveService");
	  	operations.put("save_binding", "saveBinding");
	  	operations.put("save_tmodel", "saveTModel");
	  	operations.put("delete_business", "deleteBusiness");
	  	operations.put("delete_service", "deleteService");
	  	operations.put("delete_binding", "deleteBinding");
	  	operations.put("delete_tmodel", "deleteTModel");
	  	operations.put("add_publisherassertions", "addPublisherAssertions");
	  	operations.put("set_publisherassertions", "setPublisherAssertions");
	  	operations.put("get_publisherassertions", "getPublisherAssertions");
	  	operations.put("delete_publisherassertions", "deletePublisherAssertions");
	  	operations.put("get_assertionstatusreport", "getAssertionStatusReport");
	}

	  public void validateRequest(String operation,String version,Element uddiReq)
			throws RegistryException

		{

	  	if (version == null)
	      throw new FatalErrorException(new ErrorMessage("errors.local.generic"));

	    if ((operation == null) || (operation.trim().length() == 0))
	      throw new FatalErrorException(new ErrorMessage("errors.local.operation.notidentified"));

	    else if (!operations.containsKey(operation.toLowerCase()))
	    	throw new UnsupportedException(new ErrorMessage("errors.local.publish.notsupported"));

		}

	  

	  public Node publish(Element uddiReq) throws Exception
	  {
		  Registry.start();
		  
	      //new RequestHandler on it's own thread
	      RequestHandler requestHandler = new RequestHandler();
	      requestHandler.setUddiReq(uddiReq);
	      String operation = requestHandler.getOperation(uddiReq);
	      String version   = requestHandler.getVersion(uddiReq, operation);
	      validateRequest(operation, version, uddiReq);

	      Thread thread = new Thread(requestHandler, "WorkThread");
	      thread.start();
	      thread.join();

	      if (requestHandler.getException()!=null) {
	          throw new Exception(requestHandler.getException());
	      }

	      return requestHandler.getResponse();
	  }
}
