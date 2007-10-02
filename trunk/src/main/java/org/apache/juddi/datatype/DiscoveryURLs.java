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
package org.apache.juddi.datatype;

import java.util.Vector;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DiscoveryURLs implements RegistryObject
{
  Vector discoveryURLVector;

  /**
   *
   */
  public DiscoveryURLs()
  {
  }

  /**
   *
   */
  public DiscoveryURLs(int size)
  {
    this.discoveryURLVector = new Vector(size);
  }

  /**
   *
   */
  public void addDiscoveryURL(DiscoveryURL url)
  {
    if (this.discoveryURLVector == null)
      this.discoveryURLVector = new Vector();
    this.discoveryURLVector.add(url);
  }

  /**
   *
   */
  public void setDiscoveryURLVector(Vector urls)
  {
    this.discoveryURLVector = urls;
  }

  /**
   *
   */
  public Vector getDiscoveryURLVector()
  {
    return this.discoveryURLVector;
  }

  /**
   *
   */
  public int size()
  {
    if (this.discoveryURLVector != null)
      return this.discoveryURLVector.size();
    else
      return 0;
  }
}