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
package org.apache.juddi.datatype.response;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ServiceInfos implements RegistryObject
{
  Vector serviceInfoVector;

  /**
   * default constructor
   */
  public ServiceInfos()
  {
  }

  /**
   *
   */
  public ServiceInfos(int size)
  {
    this.serviceInfoVector = new Vector(size);
  }

  /**
   *
   */
  public void addServiceInfo(ServiceInfo info)
  {
    if (this.serviceInfoVector == null)
      this.serviceInfoVector = new Vector();
    this.serviceInfoVector.add(info);
  }

  /**
   *
   */
  public void setServiceInfoVector(Vector infos)
  {
    this.serviceInfoVector = infos;
  }

  /**
   *
   */
  public Vector getServiceInfoVector()
  {
    return this.serviceInfoVector;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();

    if (this.serviceInfoVector != null)
      for (int i=0; i<this.serviceInfoVector.size(); i++)
        buffer.append(this.serviceInfoVector.elementAt(i));

    return buffer.toString();
  }
}