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
package org.apache.juddi.datatype.business;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Contacts implements RegistryObject
{
  Vector contactVector = null;

  /**
   *
   */
  public Contacts()
  {
    this.contactVector = new Vector();
  }

  /**
   *
   */
  public Contacts(int size)
  {
    this.contactVector = new Vector(size);
  }

  /**
   *
   */
  public void addContact(Contact contact)
  {
    // just return if there's nothing to add.
    if (contact == null)
      return;

    if (this.contactVector == null)
      this.contactVector = new Vector();
    this.contactVector.add(contact);
  }

  /**
   *
   */
  public void setContactVector(Vector contacts)
  {
    this.contactVector = contacts;
  }

  /**
   *
   */
  public Vector getContactVector()
  {
    return this.contactVector;
  }
}