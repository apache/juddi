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

import java.util.Vector;

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * ContactsHandler
 *
 * "Knows about the creation and populating of KeyedReference objects.
 * Returns Contacts."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ContactsHandler extends AbstractHandler
{
  public static final String TAG_NAME = "contacts";

  private HandlerMaker maker = null;

  protected ContactsHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Contacts obj = new Contacts();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,ContactHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(ContactHandler.TAG_NAME);
      obj.addContact((Contact)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Contacts contacts = (Contacts)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Vector contactVector = contacts.getContactVector();
    if ((contactVector!=null) && (contactVector.size() > 0))
    {
      handler = maker.lookup(ContactHandler.TAG_NAME);
      for (int i=0; i < contactVector.size(); i++)
        handler.marshal((Contact)contactVector.elementAt(i),element);
    }

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(ContactsHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Address address = new Address();
    address.setUseType("myAddressUseType");
    address.setSortCode("sortThis");
    address.setTModelKey(null);
    address.addAddressLine(new AddressLine("AddressLine1","keyNameAttr","keyValueAttr"));
    address.addAddressLine(new AddressLine("AddressLine2"));

    Contact contact = new Contact();
    contact.setUseType("myAddressUseType");
    contact.setPersonNameValue("Bob Whatever");
    contact.addDescription(new Description("Bob is a big fat jerk"));
    contact.addDescription(new Description("obBay sIay a igBay atFay erkJay","es"));
    contact.addEmail(new Email("bob@whatever.com"));
    contact.addPhone(new Phone("(603)559-1901"));
    contact.addAddress(address);

    Contacts contacts = new Contacts();
    contacts.addContact(contact);
    contacts.addContact(contact);

    System.out.println();

    RegistryObject regObject = contacts;
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();

    regObject = handler.unmarshal(child);
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();
  }
}