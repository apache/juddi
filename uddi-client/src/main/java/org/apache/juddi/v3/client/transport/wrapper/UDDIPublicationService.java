package org.apache.juddi.v3.client.transport.wrapper;

import java.util.HashMap;

import org.apache.juddi.v3.client.transport.InVMTransport;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetPublisherAssertions;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.SetPublisherAssertions;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class UDDIPublicationService {

	  // collection of valid operations


	private HashMap<String, Handler> operations = null;

	
	  public UDDIPublicationService() {
		super();
		operations = new HashMap<String, Handler>();
		operations.put("get_registeredInfo", new Handler("getRegisteredInfo", GetRegisteredInfo.class));
	  	operations.put("save_business", new Handler("saveBusiness", SaveBusiness.class));
	  	operations.put("save_service", new Handler("saveService", SaveService.class));
	  	operations.put("save_binding", new Handler("saveBinding", SaveBinding.class));
	  	operations.put("save_tmodel", new Handler("saveTModel", SaveTModel.class));
	  	operations.put("delete_business", new Handler("deleteBusiness", DeleteBusiness.class));
	  	operations.put("delete_service", new Handler("deleteService", DeleteService.class));
	  	operations.put("delete_binding", new Handler("deleteBinding", DeleteBinding.class));
	  	operations.put("delete_tmodel", new Handler("deleteTModel", DeleteTModel.class));
	  	operations.put("add_publisherassertions", new Handler("addPublisherAssertions", AddPublisherAssertions.class));
	  	operations.put("set_publisherassertions", new Handler("setPublisherAssertions", SetPublisherAssertions.class));
	  	operations.put("get_publisherassertions", new Handler("getPublisherAssertions", GetPublisherAssertions.class));
	  	operations.put("delete_publisherassertions", new Handler("deletePublisherAssertions", DeletePublisherAssertions.class));
	  	operations.put("get_assertionstatusreport", new Handler("getAssertionStatusReport", GetAssertionStatusReport.class));
	}

	//Verify that the appropriate endpoint was targeted for
		// this service request.  The validateRequest method will
		// throw an UnsupportedOperationException if anything's amiss.
		public void validateRequest(String operation,String version, Element uddiReq)
				throws UnsupportedOperationException
		{
		    // If the value 
		  	// specified is not "3.0" then throw an exception (this 
		  	// value must be specified for all UDDI requests and 
		  	// only version 3.0 UDDI requests are supported by 
		  	// this endpoint).
		  	if (! "3.0".equals(version))
		  		throw new UnsupportedOperationException("version needs to be 3.0");

		    if ((operation == null) || (operation.trim().length() == 0))
		    	throw new UnsupportedOperationException("operation " + operation + " not supported");
		  }

	  public Node publish(Element uddiReq) throws Exception
	  {
		  InVMTransport invmtransport = new InVMTransport();		
		  UDDIPublicationPortType publish = invmtransport.getUDDIPublishService();

	      //new RequestHandler on it's own thread
	      RequestHandler requestHandler = new RequestHandler();
	      requestHandler.setUddiReq(uddiReq);
	      requestHandler.setPortType(publish);
	      String operation = requestHandler.getOperation(uddiReq);
		  Handler opHandler = operations.get(operation);
	      requestHandler.setMethodName(opHandler.getMethodName());
		  requestHandler.setOperationClass(opHandler.getParameter());
		  
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
