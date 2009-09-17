package org.apache.juddi.v3.client.transport.wrapper;

import java.util.HashMap;

import org.apache.juddi.v3.client.transport.InVMTransport;
import org.uddi.v3_service.UDDISecurityPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.DiscardAuthToken;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class UDDISecurityService {

	// collection of valid operations
	private HashMap<String, Handler> operations = null;

	public UDDISecurityService() {
		super();
		operations = new HashMap<String, Handler>();
		operations.put("get_authToken", new Handler("getAuthToken", GetAuthToken.class));
		operations.put("discard_authToken", new Handler("discardAuthToken", DiscardAuthToken.class));
	}
	
	//Verify that the appropriate endpoint was targeted for
	// this service request.  The validateRequest method will
	// throw an UnsupportedOperationException if anything's amiss.
	public void validateRequest(String operation)
			throws UnsupportedOperationException
	{
	    if ((operation == null) || (operation.trim().length() == 0))
	    	throw new UnsupportedOperationException("operation " + operation + " not supported");
	  }

	public Node secure(Element uddiReq) throws Exception
	{
		InVMTransport invmtransport = new InVMTransport();		
        UDDISecurityPortType security = invmtransport.getUDDISecurityService();
		
		//new RequestHandler on it's own thread
		RequestHandler requestHandler = new RequestHandler();
		requestHandler.setPortType(security);
		
		String operation = requestHandler.getOperation(uddiReq);
		Handler opHandler = operations.get(operation);
	    requestHandler.setMethodName(opHandler.getMethodName());
		requestHandler.setOperationClass(opHandler.getParameter());

		String version   = requestHandler.getVersion(uddiReq, operation);
	    validateRequest(operation);
	    return requestHandler.invoke(uddiReq);
	}
}
