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
package org.apache.juddi.handler;

import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of AddressLine objects.
 * Returns AddressLine."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AddressLineHandler extends AbstractHandler
{
  public static final String TAG_NAME = "addressLine";

  private HandlerMaker maker = null;

  protected AddressLineHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AddressLine obj = new AddressLine();

    // Attributes
    String keyName = element.getAttribute("keyName");
    if ((keyName != null) && (keyName.trim().length() > 0))
      obj.setKeyName(keyName);

    String keyValue = element.getAttribute("keyValue");
    if ((keyValue != null) && (keyValue.trim().length() > 0))
      obj.setKeyValue(keyValue);

    // Text Node Value
    obj.setLineValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AddressLine line = (AddressLine)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);

    String keyName = line.getKeyName();
    if ((keyName != null) && (keyName.trim().length() > 0))
      element.setAttribute("keyName",keyName);

    String keyValue = line.getKeyValue();
    if ((keyValue != null) && (keyValue.trim().length() > 0))
      element.setAttribute("keyValue",keyValue);

    String lineValue = line.getLineValue();
    if (lineValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(lineValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    // create the source object
    AddressLine lineIn = new AddressLine("AddressLine1","keyNameAttr","keyValueAttr");

    // unmarshal & marshal (object->xml->object)
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(AddressLineHandler.TAG_NAME);
    Element element = null;
    handler.marshal(lineIn,element);
    AddressLine lineOut = (AddressLine)handler.unmarshal(element);

    // compare unmarshaled with marshaled obj
    if (lineOut.equals(lineIn))
      System.out.println("Input and output are the same.");
    else
      System.out.println("Input and output are NOT the same!");
  }
}