/*
 * Copyright (C) 2004, Liberty Mutual Group
 * All Rights Reserved
 */

package org.apache.juddi.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.messaging.URLEndpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SOAPTransport implements Transport
{
    // jUDDI logger
    private static Log log = LogFactory.getLog(SOAPTransport.class);
    
    // XML Document Builder
    private static DocumentBuilder docBuilder = null;

    // jUDDI Proxy Properties
    private URL inquiryURL;
    private URL publishURL;
    private URL adminURL;
    private String securityProvider;
    private String protocolHandler;
    private String uddiVersion;
    private String uddiNamespace;  

    public String send(String request,URL endpointURL)
        throws RegistryException
    {    
        String response = null;
    
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(request.getBytes());
            Document reqDoc = getDocumentBuilder().parse(bais);
            Element reqElem = reqDoc.getDocumentElement();            
            Element resElem = send(reqElem,endpointURL);
            response = XMLUtils.toString(resElem);
        }
        catch (SAXException sex) {
            sex.printStackTrace();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
        
        return response;
    }    

    public Element send(Element request,URL endpointURL) 
        throws RegistryException
    {    
        Element response = null;
        
        try 
        {
            XMLUtils.writeXML((Element)request,System.out);   

            // Create a new XML document to store the UDDI 
            // request into.
              
            DocumentBuilder docBuilder = getDocumentBuilder();
            Document document = docBuilder.newDocument();
            document.importNode((Node)request,true);
            
            XMLUtils.writeXML((Element)document.getFirstChild(),System.out);   
            
            // Create a new SOAP message and append this 
            // document to the SOAP request body

            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage reqMsg = msgFactory.createMessage();
            SOAPBody reqBody = reqMsg.getSOAPBody();
            reqBody.addDocument(document);
            
            // Create the web service endpoint
            
            URLEndpoint endpoint = new URLEndpoint(endpointURL.toExternalForm());
            reqMsg.saveChanges();
            reqMsg.writeTo(System.out);

            // Create the connection factory and call 
            // the web service
            
            SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = scf.createConnection();
            SOAPMessage resMsg = connection.call(reqMsg,endpoint);
            System.out.println("Received reply from: " + endpointURL);
            connection.close();
            resMsg.writeTo(System.out);

            // Close the connection
            
            SOAPBody resBody = resMsg.getSOAPBody();
            response = (Element)resBody.getFirstChild();      
        }
        catch (SOAPException sex) {
            sex.printStackTrace();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
        
        return response;
    }
  
    /**
     *
     */
    private DocumentBuilder getDocumentBuilder()
    {
        if (docBuilder == null)
            docBuilder = createDocumentBuilder();    
        return docBuilder;
    }

    /**
     *
     */
    private synchronized DocumentBuilder createDocumentBuilder()
    {
        if (docBuilder != null)
            return docBuilder;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //factory.setNamespaceAware(true);
            //factory.setValidating(true);

            docBuilder = factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException pcex) {
            pcex.printStackTrace();
        }

        return docBuilder;
    }
}
