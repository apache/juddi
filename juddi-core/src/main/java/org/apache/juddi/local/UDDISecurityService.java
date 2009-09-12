package org.apache.juddi.local;

import java.util.HashMap;
import java.util.TreeSet;

import org.apache.juddi.Registry;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.uddi.api_v3.client.transport.InVMTransport;
import org.uddi.v3_service.UDDISecurityPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class UDDISecurityService {

	// collection of valid operations
	private HashMap<String, String> operations = null;

	public UDDISecurityService() {
		super();
		operations = new HashMap<String, String>();
		operations.put("get_authtoken", "getAuthToken");
		operations.put("discard_authToken", "discardAuthToken");
	}

	public void validateRequest(String operation,String version,Element uddiReq)
		throws RegistryException
	{
		if (version == null)
	    	throw new FatalErrorException(new ErrorMessage("errors.local.generic"));

		if ((operation == null) || (operation.trim().length() == 0))
			throw new FatalErrorException(new ErrorMessage("errors.local.operation.notidentified"));

		else if (!operations.containsKey(operation.toLowerCase()))
	    	throw new UnsupportedException(new ErrorMessage("errors.local.security.notsupported"));

	}

	public Node secure(Element uddiReq) throws Exception
	{
		Registry.start();
		InVMTransport invmtransport = new InVMTransport();		
        UDDISecurityPortType security = invmtransport.getUDDISecurityService();
		
		//new RequestHandler on it's own thread
		RequestHandler requestHandler = new RequestHandler();
		requestHandler.setUddiReq(uddiReq);
		requestHandler.setPortType(security);
		
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
