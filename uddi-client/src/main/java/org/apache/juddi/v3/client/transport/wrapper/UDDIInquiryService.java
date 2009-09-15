package org.apache.juddi.v3.client.transport.wrapper;

import java.util.HashMap;

import org.apache.juddi.v3.client.transport.InVMTransport;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class UDDIInquiryService {
	private static final long serialVersionUID = 1L;
	//private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	private HashMap<String, Handler> operations = null;

	public UDDIInquiryService() {
		super();
		operations = new HashMap<String, Handler>();
		operations.put("find_business", new Handler("findBusiness", FindBusiness.class));
		operations.put("find_service", new Handler("findService", FindService.class));
		operations.put("find_binding", new Handler("findBinding", FindBinding.class));
		operations.put("find_tModel", new Handler ("findTModel", FindTModel.class));
		operations.put("find_relatedBusinesses", new Handler("findRelatedBusinesses", FindRelatedBusinesses.class));
		operations.put("get_businessDetail", new Handler("getBusinessDetail", GetBusinessDetail.class));
		operations.put("get_serviceDetail", new Handler("getServiceDetail", GetServiceDetail.class));
		operations.put("get_bindingDetail", new Handler("getBindingDetail", GetBindingDetail.class));
		operations.put("get_tModelDetail", new Handler("getTModelDetail", GetTModelDetail.class));
		operations.put("get_operationalInfo", new Handler("getOperationalInfo", GetOperationalInfo.class));
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
	  	// only version 2.0 and 3.0 UDDI requests are supported by 
	  	// this endpoint).
	  	if (version == null)
	  		throw new UnsupportedOperationException("version needs to be 3.0");

	    if ((operation == null) || (operation.trim().length() == 0))
	    	throw new UnsupportedOperationException("operation " + operation + " not supported");
	  }

	  public Node inquire(Element uddiReq) throws Exception{		  
		  InVMTransport invmtransport = new InVMTransport();		
	      UDDIInquiryPortType inquiry = invmtransport.getUDDIInquiryService();

	      //new RequestHandler on it's own thread
	      RequestHandler requestHandler = new RequestHandler();
	      requestHandler.setUddiReq(uddiReq);
	      requestHandler.setPortType(inquiry);
	      
	      String operation = requestHandler.getOperation(uddiReq);
		  Handler opHandler = operations.get(operation);
	      requestHandler.setMethodName(opHandler.getMethodName());
		  requestHandler.setOperationClass(opHandler.getParameter());

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
