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
