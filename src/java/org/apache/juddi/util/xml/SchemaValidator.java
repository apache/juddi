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

/**
 * @author anou_mana@users.sourceforge.net
 */
import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SchemaValidator
{
  public static void main(String[] args)
  {
    String parserClass = "org.apache.xerces.parsers.SAXParser";
    String validationFeature = "http://xml.org/sax/features/validation";
    String schemaFeature = "http://apache.org/xml/features/validation/schema";
    try
    {
      String x = args[0];
      XMLReader r = XMLReaderFactory.createXMLReader(parserClass);
      r.setFeature(validationFeature, true);
      r.setFeature(schemaFeature, true);
      r.setErrorHandler(new MyErrorHandler());
      r.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation","C:/Projects/sample/xsd/dictionary.xsd");
      r.parse(x);
    }
    catch (SAXException e)
    {
      System.out.println(e.toString());
    }
    catch (IOException e)
    {
      System.out.println(e.toString());
    }
  }
  private static class MyErrorHandler extends DefaultHandler
  {
    public void warning(SAXParseException e) throws SAXException
    {
      System.out.println("Warning: ");
      printInfo(e);
    }
    public void error(SAXParseException e) throws SAXException
    {
      System.out.println("Error: ");
      printInfo(e);
    }
    public void fatalError(SAXParseException e) throws SAXException
    {
      System.out.println("Fattal error: ");
      printInfo(e);
    }
    private void printInfo(SAXParseException e)
    {
      System.out.println("   Public ID: " + e.getPublicId());
      System.out.println("   System ID: " + e.getSystemId());
      System.out.println("   Line number: " + e.getLineNumber());
      System.out.println("   Column number: " + e.getColumnNumber());
      System.out.println("   Message: " + e.getMessage());
    }
  }
}
