/*
 * Copyright 2001-2010 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.v3.client.transport.wrapper;

import java.util.HashMap;

import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDISecurityPortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class UDDISecurityService {

	private final static String DEFAULT_NODE_NAME = "default";

	private String clientName = null;
	private String nodeName = null;
	// collection of valid operations
	private HashMap<String, Handler> operations = null;

	public UDDISecurityService() {
		super();
		clientName = System.getProperty("org.apache.juddi.v3.client.name"); 
		nodeName    = System.getProperty("org.apache.juddi.v3.client.node.name",DEFAULT_NODE_NAME);
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
	
	public Node secure(Element uddiReq) throws Exception {
	    return secure(uddiReq, nodeName, clientName);
	}

	public Node secure(Element uddiReq, String nodeName, String clientName) throws Exception
	{
	    UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
	    String clazz = client.getClientConfig().getUDDINode(nodeName).getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, this.getClass());
            Transport transport = (Transport) transportClass.getConstructor(String.class, String.class).newInstance(clientName, nodeName);
	    UDDISecurityPortType security = transport.getUDDISecurityService();

	    //new RequestHandler on it's own thread
	    RequestHandler requestHandler = new RequestHandler();
	    requestHandler.setPortType(security);

	    String operation = requestHandler.getOperation(uddiReq);
	    Handler opHandler = operations.get(operation);
	    if (opHandler == null) {
	        throw new IllegalArgumentException("Operation not found: " + operation);
	    }

	    requestHandler.setMethodName(opHandler.getMethodName());
	    requestHandler.setOperationClass(opHandler.getParameter());

	    @SuppressWarnings("unused")
	    String version   = requestHandler.getVersion(uddiReq, operation);
	    validateRequest(operation);
	    return requestHandler.invoke(uddiReq);
	}
}
