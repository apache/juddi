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
package org.apache.juddi.handler;


/**
 * Base class for the request handler structures.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class AbstractHandler implements IHandler
{
//  /**
//   *
//   * @param element
//   * @return String
//   */
//  protected String getText(Element element)
//  {
//    StringBuffer textBuffer = new StringBuffer();
//
//    NodeList nodeList = element.getChildNodes();
//    for (int i=0; i<nodeList.getLength(); i++)
//    {
//      if (nodeList.item(i).getNodeType() == Element.TEXT_NODE)
//        textBuffer.append(nodeList.item(i).getNodeValue());
//    }
//
//    return textBuffer.toString().trim();
//  }
//
//  /**
//   *
//   * @param element
//   * @param tagName
//   * @return Vector
//   */
//  public Vector getChildElementsByTagName(Element element,String tagName)
//  {
//    Vector result = new Vector();
//
//    NodeList children = element.getChildNodes();
//    for (int i=0; i<children.getLength(); i++)
//    {
//      Node node = children.item(i);
//      if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tagName))
//        result.addElement(node); // matching element
//    }
//
//    return result;
//  }
//
//  /**
//   * create a new empty xml element
//   * @returns a new org.w3c.Element named "root"
//   */
//  protected Element newRootElement()
//  {
//    Element element = null;
//
//    try
//    {
//      javax.xml.parsers.DocumentBuilderFactory factory =
//        javax.xml.parsers.DocumentBuilderFactory.newInstance();
//
//      javax.xml.parsers.DocumentBuilder builder =
//        factory.newDocumentBuilder();
//
//      Document document = builder.newDocument();
//      Element holder = document.createElement("root");
//      document.appendChild(holder);
//      element = document.getDocumentElement();
//    }
//    catch(Exception ex) { ex.printStackTrace(); }
//
//    return element;
//  }
}