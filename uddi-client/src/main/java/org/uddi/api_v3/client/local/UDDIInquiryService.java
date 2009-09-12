package org.uddi.api_v3.client.local;

import java.util.HashMap;

import org.apache.juddi.Registry;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.uddi.api_v3.client.transport.InVMTransport;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class UDDIInquiryService {
	private static final long serialVersionUID = 1L;
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	private HashMap<String, String> operations = null;

	public UDDIInquiryService() {
		super();
		operations = new HashMap<String, String>();
		operations.put("find_business", "findBusiness");
		operations.put("find_service", "findService");
		operations.put("find_binding", "findBinding");
		operations.put("find_tmodel", "findTModel");
		operations.put("find_relatedbusinesses", "findRelatedBusinesses");
		operations.put("get_businessdetail", "getBusinessDetail");
		operations.put("get_servicedetail", "getServiceDetail");
		operations.put("get_bindingdetail", "getBindingDetail");
		operations.put("get_tmodeldetail", "getTModelDetail");
		operations.put("get_operationalInfo", "getOperationalInfo");
	}


	//Verify that the appropriate endpoint was targeted for
	// this service request.  The validateRequest method will
	// throw an UnsupportedException if anything's amiss.
	public void validateRequest(String operation,String version, Element uddiReq)
			throws RegistryException
	{
	    // If the value 
	  	// specified is not "2.0" then throw an exception (this 
	  	// value must be specified for all UDDI requests and 
	  	// only version 2.0 UDDI requests are supported by 
	  	// this endpoint).
	  	if (version == null)
	  		throw new FatalErrorException(new ErrorMessage("errors.local.generic"));

	    if ((operation == null) || (operation.trim().length() == 0))
	    	throw new FatalErrorException(new ErrorMessage("errors.local.operation.notidentified"));

	    else if (!operations.containsKey(operation.toLowerCase()))
	    	throw new UnsupportedException(new ErrorMessage("errors.local.inquiry.notsupported"));
		}

	  public Node inquire(Element uddiReq) throws Exception{
		  
		  Registry.start();
		  InVMTransport invmtransport = new InVMTransport();		
	      UDDIInquiryPortType inquiry = invmtransport.getUDDIInquiryService();

	      //new RequestHandler on it's own thread
	      RequestHandler requestHandler = new RequestHandler();
	      requestHandler.setUddiReq(uddiReq);
	      requestHandler.setPortType(inquiry);
	      
	      String operation = requestHandler.getOperation(uddiReq);
	      String version   = requestHandler.getVersion(uddiReq,operation);
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
