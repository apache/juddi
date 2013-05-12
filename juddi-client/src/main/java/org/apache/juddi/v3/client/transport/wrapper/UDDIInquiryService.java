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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class UDDIInquiryService {
	
	private final static String DEFAULT_NODE_NAME = "default";

	private String clientName = null;
	private String nodeName = null;
	private HashMap<String, Handler> operations = null;

	public UDDIInquiryService() {
		super();
		clientName = System.getProperty("org.apache.juddi.v3.client.client.name");
		nodeName    = System.getProperty("org.apache.juddi.v3.client.node.name",DEFAULT_NODE_NAME);
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
	public void validateRequest(String operation)
	    throws UnsupportedOperationException
	{
	    if ((operation == null) || (operation.trim().length() == 0))
	    	throw new UnsupportedOperationException("operation " + operation + " not supported");
	}

	public Node inquire(Element uddiReq) throws Exception {
	    return inquire(uddiReq, nodeName, clientName);
	}
	
	public Node inquire(Element uddiReq, String nodeName, String clientName) throws Exception {
	    UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
	    String clazz = client.getClientConfig().getUDDINode(nodeName).getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz,this.getClass());
            Transport transport = (Transport) transportClass.getConstructor(String.class, String.class).newInstance(clientName, nodeName);
            UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();

	    //new RequestHandler on it's own thread
	    RequestHandler requestHandler = new RequestHandler();
	    requestHandler.setPortType(inquiry);

	    String operation = requestHandler.getOperation(uddiReq);
	    Handler opHandler = operations.get(operation);
	    if (opHandler == null) {
	        throw new IllegalArgumentException("Operation not found: " + operation);
	    }
	    requestHandler.setMethodName(opHandler.getMethodName());
	    requestHandler.setOperationClass(opHandler.getParameter());

	    @SuppressWarnings("unused")
	    String version   = requestHandler.getVersion(uddiReq,operation);
	    validateRequest(operation);
	    return requestHandler.invoke(uddiReq);
	}

	public String inquire(UDDIInquiryPortType inquiry, String request) throws Exception {
	    java.io.InputStream sbis = new ByteArrayInputStream(request.getBytes());
	    javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    dbf.setValidating(false);
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.parse(sbis);
	    Element reqElem = doc.getDocumentElement();

	    RequestHandler requestHandler = new RequestHandler();
	    requestHandler.setPortType(inquiry);

	    String operation = reqElem.getTagName().toString();
	    Handler opHandler = operations.get(operation);
            if (opHandler == null) {
                throw new IllegalArgumentException("Operation not found: " + operation);
            }

	    requestHandler.setMethodName(opHandler.getMethodName());
	    requestHandler.setOperationClass(opHandler.getParameter());

	    Node n = requestHandler.invoke(reqElem);

	    StringWriter sw = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
	    t.transform(new DOMSource(n), new StreamResult(sw));
	    return sw.toString();
	}
}
