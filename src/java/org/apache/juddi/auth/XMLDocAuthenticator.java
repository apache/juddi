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
package org.apache.juddi.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnknownUserException;
import org.apache.juddi.util.Config;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * This is a simple implementation of jUDDI's Authenticator interface. The credential
 * store is simply an unencrypted xml document called 'juddi.users' that can be
 * found in jUDDI's config directory. Below is an example of what you might find
 * in this document.
 *
 *     Example juddi.users document:
 *     =============================
 *     <?xml version="1.0" encoding="UTF-8"?>
 *     <juddi-users>
 *       <user userid="sviens" password="password" />
 *       <user userid="griddell" password="password" />
 *       <user userid="bhablutzel" password="password" />
 *     </juddi-users>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class XMLDocAuthenticator implements ContentHandler, ErrorHandler, Authenticator
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(XMLDocAuthenticator.class);

  // static default juddi users file name
  private static final String FILE_NAME_KEY = "juddi.users";
  private static final String DEFAULT_FILE_NAME = "juddi-users.xml";

  // hashtable of UserInfo objects
  Hashtable userTable;

  class UserInfo
  {
    public String userid;
    public String password;

    public String toString()
    {
      StringBuffer buff = new StringBuffer(75);
      buff.append(userid);
      buff.append(" | ");
      buff.append(password);
      return buff.toString();
    }
  }

  /**
   *
   */
  public XMLDocAuthenticator()
  {
    init();
  }

  /**
   * Perform auth initialization tasks
   */
  public synchronized void init()
  {
    // create and populate a Hashtable of UserInfo objects (one per user)
    try {
      userTable = new Hashtable();

      String usersFileName =
        Config.getStringProperty(FILE_NAME_KEY,DEFAULT_FILE_NAME);

      log.info("Using jUDDI Users File: "+usersFileName);

      build(new FileInputStream(usersFileName));
    }
    catch (IOException ioex) {
      ioex.printStackTrace();
    }
    catch (SAXException saxex) {
      saxex.printStackTrace();
    }
    catch (ParserConfigurationException pcex) {
      pcex.printStackTrace();
    }
  }

  /**
   *
   */
  public String authenticate(String userID,String credential)
    throws RegistryException
  {
    // a userID must be specified.
    if (userID == null)
      throw new UnknownUserException("Invalid user ID = "+userID);

    // credential (password) must be specified.
    if (credential == null)
      throw new UnknownUserException("Invalid credentials");

    if (userTable.containsKey(userID))
    {
      UserInfo userInfo = (UserInfo)userTable.get(userID);
      if ((userInfo.password == null) || (!credential.equals(userInfo.password)))
         throw new UnknownUserException("Invalid credentials");
    }
    else
      throw new UnknownUserException("Invalid user ID: "+userID);

    return userID;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buff = new StringBuffer(100);

    Enumeration enum = userTable.keys();
    while (enum.hasMoreElements())
    {
      UserInfo userInfo = (UserInfo)userTable.get(enum.nextElement());
      buff.append(userInfo.toString()+"\n");
    }

    return buff.toString();
  }

  /**
   *
   */
  Hashtable build(InputStream istream)
    throws ParserConfigurationException,SAXException,IOException
  {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);

    XMLReader xr = spf.newSAXParser().getXMLReader();
    xr.setContentHandler(this);
    xr.setErrorHandler(this);
    xr.parse(new InputSource(istream));

    return (Hashtable)this.getObject();
  }

  /**
   * handle setDocumentLocator event
   */
  public void setDocumentLocator(org.xml.sax.Locator locator)
  {
  }

  /**
   * handle startDocument event
   */
  public void startDocument()
    throws SAXException
  {
  }

  /**
   * handle endDocument event
   */
  public void endDocument()
    throws SAXException
  {
  }

  /**
   * handle startElement event
   */
  public void startElement(String uri,String name,String qName,Attributes attributes)
    throws SAXException
  {
    if (name.equalsIgnoreCase("user"))
    {
      UserInfo userInfo = new UserInfo();

      for(int i=0; i<attributes.getLength(); i++)
      {
        if (attributes.getQName(i).equalsIgnoreCase("userid"))
          userInfo.userid = attributes.getValue(i);
        else if (attributes.getQName(i).equalsIgnoreCase("password"))
          userInfo.password = attributes.getValue(i);
      }

      userTable.put(userInfo.userid,userInfo);
    }
  }

  /**
   * handle endElement event
   */
  public void endElement(String name,String string2,String string3)
    throws SAXException
  {
  }

  /**
   * handle characters event
   */
  public void characters(char[] chars,int int1, int int2)
    throws SAXException
  {
  }

  /**
   * handle ignorableWhitespace event
   */
  public void ignorableWhitespace(char[] chars,int int1, int int2)
    throws SAXException
  {
  }

  /**
   * handle processingInstruction event
   */
  public void processingInstruction(String string1,String string2)
    throws SAXException
  {
  }

  /**
   * handle startPrefixMapping event
   */
  public void startPrefixMapping(String string1,String string2)
    throws SAXException
  {
  }

  /**
   * handle endPrefixMapping event
   */
  public void endPrefixMapping(String string)
    throws SAXException
  {
  }

  /**
   * handle skippedEntity event
   */
  public void skippedEntity(String string)
    throws SAXException
  {
  }

  /**
   * handle warning event
   */
  public void warning(SAXParseException spex)
    throws SAXException
  {
  }

  /**
   * handle error event
   */
  public void error(SAXParseException spex)
    throws SAXException
  {
  }

  /**
   * handle fatalError event
   */
  public void fatalError(SAXParseException spex)
    throws SAXException
  {
  }

  /**
   * Retrieve the object built by the handling of SAX events.
   */
  Object getObject()
  {
    return this.userTable;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    Authenticator auth = new XMLDocAuthenticator();

    try {
      System.out.print("anou_mana/password: ");
      auth.authenticate("anou_mana","password");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("anou_mana/badpass: ");
      auth.authenticate("anou_mana","badpass");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("bozo/clown: ");
      auth.authenticate("bozo","clown");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("sviens/password: ");
      auth.authenticate("sviens","password");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}