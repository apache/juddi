/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */
package org.apache.juddi.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.juddi.util.Loader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class XMLUtils
{
  // jUDDI XML document builder
  private static DocumentBuilder docBuilder = null;

  /**
   *
   * @param element
   * @return String
   */
  public static String getText(Element element)
  {
    StringBuffer textBuffer = new StringBuffer();

    NodeList nodeList = element.getChildNodes();
    for (int i=0; i<nodeList.getLength(); i++)
    {
      if (nodeList.item(i).getNodeType() == Element.TEXT_NODE)
        textBuffer.append(nodeList.item(i).getNodeValue());
    }

    return textBuffer.toString().trim();
  }

  /**
   *
   * @param element
   * @param tagName
   * @return Vector
   */
  public static Vector getChildElementsByTagName(Element element,String tagName)
  {
    Vector result = new Vector();

    NodeList children = element.getChildNodes();
    for (int i=0; i<children.getLength(); i++)
    {
      //System.out.println("node name:       "+node.getNodeName());
      //System.out.println("node local name: "+node.getLocalName());
        
      Node node = children.item(i);      
      String nodeName = node.getNodeName();
      String localName = node.getLocalName();
      
      if ((localName == null) && (nodeName != null))
          localName = nodeName;
            
      if (node.getNodeType() == Node.ELEMENT_NODE && localName.equals(tagName))
        result.addElement(node); // matching element
    }

    return result;
  }

  /**
   * create a new empty xml element
   * @return a new org.w3c.Element named "root"
   */
  public static Element newRootElement()
  {
    Element element = null;

    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      Element holder = document.createElement("root");
      document.appendChild(holder);
      element = document.getDocumentElement();
    }
    catch(Exception ex) { ex.printStackTrace(); }

    return element;
  }

  public static Document createDocument()
  {
    if (docBuilder == null)
      docBuilder = createDocumentBuilder();

    return docBuilder.newDocument();
  }

  private static DocumentBuilder createDocumentBuilder()
  {
    if (docBuilder != null)
      return docBuilder;

    try {
     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     docBuilder = factory.newDocumentBuilder();
    }
    catch(ParserConfigurationException pcex) {
      pcex.printStackTrace();
    }

    return docBuilder;
  }

  public static void writeXML(Element element,OutputStream stream)
  {
    try {
      TransformerFactory xformerFactory = TransformerFactory.newInstance();
      Transformer xformer = xformerFactory.newTransformer();
      Result output = new StreamResult(stream);
      DOMSource source = new DOMSource(element);
      
      // print the xml to the specified OutputStream
      xformer.transform(source,output);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
    
  public static String toString(Element element)
  {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();      
      writeXML(element,stream);
      
      return stream.toString();      
  }
  
  public static void validate(URL xmlDocUrl) 
  {    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);    
    factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
    factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",Loader.getResource("uddi_v2.xsd"));

    try
    {
        DocumentBuilder builder = factory.newDocumentBuilder();
        Validator handler = new Validator();
        builder.setErrorHandler(handler); 
        //builder.parse(xmlDocUrl);
    
        if ((handler.error) || (handler.warning))
            System.out.println(handler.toString());
    }
    catch(ParserConfigurationException pcex) {
        pcex.printStackTrace();
    }
  }
}

class Validator extends DefaultHandler
{
  public boolean warning = false;
  public boolean error = false;  
  public SAXParseException exception = null;
    
  public void warning(SAXParseException spex) 
      throws SAXException
  {
    warning = true;
    exception = spex;        
  }
  
  public void error(SAXParseException spex) 
      throws SAXException
  {
    error = true;
    exception = spex;        
  }
  
  public void fatalError(SAXParseException spex) 
      throws SAXException
  {
    error = true;
    exception = spex;        
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    
    if (exception != null)
    {
      buffer.append("Public ID: " + exception.getPublicId());
      buffer.append("\n");
      buffer.append("System ID: " + exception.getSystemId());
      buffer.append("\n");
      buffer.append("Line number: " + exception.getLineNumber());
      buffer.append("\n");
      buffer.append("Column number: " + exception.getColumnNumber());
      buffer.append("\n");
      buffer.append("Message: " + exception.getMessage());
    }
    
    return buffer.toString();
  }
}