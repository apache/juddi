/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.util.xml;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
      Node node = children.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE && node.getLocalName().equals(tagName))
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
      javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();

      javax.xml.parsers.DocumentBuilder builder =
        factory.newDocumentBuilder();

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

  /**
   * Write XML data to an OutputStream
   *
   * @param element XML Element to write
   * @param stream OutputStream to write to
   */
  public static void writeXML(Element element,OutputStream stream)
  {
    org.apache.axis.utils.XMLUtils.ElementToStream(element,stream);
  }

  /**
   * Write XML data to a Writer
   *
   * @param element XML Element to write
   * @param stream Writer to write to
   */
  public static void writeXML(Element element,Writer writer)
  {
    org.apache.axis.utils.XMLUtils.ElementToWriter(element,writer);
  }
}