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
package org.apache.juddi.datatype.response;

import java.util.Vector;

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.SharedRelationships;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Manavalan
 */
public class RelatedBusinessInfo implements RegistryObject
{
  String businessKey;
  Vector nameVector;
  Vector descVector;
  SharedRelationships sharedRelationships;

  /**
   * default constructor
   */
  public RelatedBusinessInfo()
  {
  }

  /**
   * copy constructor
   */
  public RelatedBusinessInfo(RelatedBusinessInfo bizInfo)
  {
    businessKey = bizInfo.getBusinessKey();
    nameVector = bizInfo.getNameVector();
    descVector = bizInfo.getDescriptionVector();
    sharedRelationships = bizInfo.getSharedRelationships();
  }

  /**
   *
   */
  public RelatedBusinessInfo(String key)
  {
    this.businessKey = key;
  }

  /**
   *
   */
  public void setBusinessKey(String key)
  {
    this.businessKey = key;
  }

  /**
   *
   */
  public String getBusinessKey()
  {
    return this.businessKey;
  }

  /**
   *
   */
  public void addName(Name name)
  {
    if (this.nameVector == null)
      this.nameVector = new Vector();
    this.nameVector.add(name);
  }

  /**
   *
   */
  public void setNameVector(Vector names)
  {
    this.nameVector = names;
  }

  /**
   *
   */
  public Vector getNameVector()
  {
    return this.nameVector;
  }

  /**
   *
   */
  public void addDescription(Description desc)
  {
    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(desc);
  }

  /**
   *
   */
  public void setDescriptionVector(Vector descriptions)
  {
    this.descVector = descriptions;
  }

  /**
   *
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   *
   */
  public void addKeyedReference(KeyedReference ref)
  {
    if (this.sharedRelationships == null)
      this.sharedRelationships = new SharedRelationships();
    this.sharedRelationships.addKeyedReference(ref);
  }

  /**
   * @return SharedRelationships
   */
  public SharedRelationships getSharedRelationships()
  {
    return this.sharedRelationships;
  }

  /**
   * @param relationships
   */
  public void setSharedRelationships(SharedRelationships relationships)
  {
    this.sharedRelationships = relationships;
  }

  /**
   *
   */
  public void setBusinessKey(BusinessKey businessKey)
  {
    if ((businessKey != null) && (businessKey.getValue() != null))
    this.setBusinessKey(businessKey.getValue());
  }

}