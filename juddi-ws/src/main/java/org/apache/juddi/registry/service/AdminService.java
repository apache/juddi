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
package org.apache.juddi.registry.service;

import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.juddi.IRegistry;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AdminService extends AbstractService 
{
  // collection of valid operations
  private TreeSet operations = null;

  public void init(ServletConfig config) 
    throws ServletException
  {
    super.init(config); 
  	
  	operations = new TreeSet();
  	operations.add("get_registryinfo");
  	operations.add("find_publisher");
  	operations.add("get_publisherdetail");
  	operations.add("save_publisher");
  	operations.add("delete_publisher");
  }
  
  public void validateRequest(String operation,String version,Element uddiReq)
		throws RegistryException
	{
    // Grab the generic attribute value.  If one isn't 
    // specified or the value specified is not "1.0" then 
    // throw an exception (this value must be specified 
    // for all UDDI requests and the endpoint called only 
    // supports UDDI version 1.0 requests).

    if (version == null)
      throw new FatalErrorException("A jUDDI generic attribute " +
        "value was not found for UDDI request: "+operation+" (The " +
        "'generic' attribute must be present)");
    else if (!version.equals(IRegistry.JUDDI_V1_GENERIC))
      throw new UnsupportedException("Only jUDDI v1 requests " +
        "are currently supported. The generic attribute value " +
        "received was: "+version);

    if ((operation == null) || (operation.trim().length() == 0))
      throw new FatalErrorException("The jUDDI service operation " +
        "could not be identified.");
    else if (!operations.contains(operation.toLowerCase()))
    	throw new UnsupportedException("The operation "+operation+" is not " +
    			"supported by the jUDDI Admin API.");
	}
}
