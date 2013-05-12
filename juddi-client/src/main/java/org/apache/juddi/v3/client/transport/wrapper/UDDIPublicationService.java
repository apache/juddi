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
	private final static String DEFAULT_NODE_NAME = "default";

	private String clientName = null;
	private String nodeName = null;
	// collection of valid operations
	private HashMap<String, Handler> operations = null;


	  public UDDIPublicationService() {
		super();
		clientName = System.getProperty("org.apache.juddi.v3.client.client.name");
		nodeName    = System.getProperty("org.apache.juddi.v3.client.node.name",DEFAULT_NODE_NAME);
		operations = new HashMap<String, Handler>();
		operations.put("get_registeredInfo", new Handler("getRegisteredInfo", GetRegisteredInfo.class));
	  	operations.put("save_business", new Handler("saveBusiness", SaveBusiness.class));
	  	operations.put("save_service", new Handler("saveService", SaveService.class));
	  	operations.put("save_binding", new Handler("saveBinding", SaveBinding.class));
	  	operations.put("save_tModel", new Handler("saveTModel", SaveTModel.class));
	  	operations.put("delete_business", new Handler("deleteBusiness", DeleteBusiness.class));
	  	operations.put("delete_service", new Handler("deleteService", DeleteService.class));
	  	operations.put("delete_binding", new Handler("deleteBinding", DeleteBinding.class));
	  	operations.put("delete_tModel", new Handler("deleteTModel", DeleteTModel.class));
	  	operations.put("add_publisherAssertions", new Handler("addPublisherAssertions", AddPublisherAssertions.class));
	  	operations.put("set_publisherAssertions", new Handler("setPublisherAssertions", SetPublisherAssertions.class));
	  	operations.put("get_publisherAssertions", new Handler("getPublisherAssertions", GetPublisherAssertions.class));
	  	operations.put("delete_publisherAssertions", new Handler("deletePublisherAssertions", DeletePublisherAssertions.class));
	  	operations.put("get_assertionStatusReport", new Handler("getAssertionStatusReport", GetAssertionStatusReport.class));
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

	public Node publish(Element uddiReq) throws Exception {
	    return publish(uddiReq, nodeName, clientName);
	}
	
	public Node publish(Element uddiReq, String nodeName, String clientName) throws Exception
	{
	    UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
	    String clazz = client.getClientConfig().getUDDINode(nodeName).getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, this.getClass());
            Transport transport = (Transport) transportClass.getConstructor(String.class, String.class).newInstance(clientName, nodeName);
            UDDIPublicationPortType publish = transport.getUDDIPublishService();

	    //new RequestHandler on it's own thread
	    RequestHandler requestHandler = new RequestHandler();
	    requestHandler.setPortType(publish);
	    String operation = requestHandler.getOperation(uddiReq);
	    Handler opHandler = operations.get(operation);
	    if (opHandler == null) {
	        throw new IllegalArgumentException("Operation not found : " + operation);
	    }

	    requestHandler.setMethodName(opHandler.getMethodName());
	    requestHandler.setOperationClass(opHandler.getParameter());

	    @SuppressWarnings("unused")
	    String version   = requestHandler.getVersion(uddiReq, operation);
	    validateRequest(operation);

	    Node temp = requestHandler.invoke(uddiReq);

	    return temp;
	}
}
