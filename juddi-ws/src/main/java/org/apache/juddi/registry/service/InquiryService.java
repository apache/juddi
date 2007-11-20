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
public class InquiryService extends AbstractService
{
  // collection of valid operations
  private TreeSet operations = null;

  public void init(ServletConfig config) 
  	throws ServletException
	{
  	super.init(config); 
  	
  	operations = new TreeSet();
  	operations.add("find_business");
  	operations.add("find_service");
  	operations.add("find_binding");
  	operations.add("find_tmodel");
  	operations.add("find_relatedbusinesses");
  	operations.add("get_businessdetail");
  	operations.add("get_businessdetailext");
  	operations.add("get_servicedetail");
  	operations.add("get_bindingdetail");
  	operations.add("get_tmodeldetail");
	}

  public void validateRequest(String operation,String version,Element uddiReq)
		throws RegistryException
	{
    // If the value 
  	// specified is not "2.0" then throw an exception (this 
  	// value must be specified for all UDDI requests and 
  	// only version 2.0 UDDI requests are supported by 
  	// this endpoint).

  	if (version == null)
      throw new FatalErrorException("A UDDI generic attribute " +
        "value was not found for UDDI request: "+operation+" (The " +
        "'generic' attribute must be present)");
    else if (!version.equals(IRegistry.UDDI_V2_GENERIC))
      throw new UnsupportedException("Only UDDI v2 " +
        "requests are currently supported. The generic attribute value " +
        "received was: "+version);

    if ((operation == null) || (operation.trim().length() == 0))
      throw new FatalErrorException("The UDDI service operation " +
        "could not be identified.");
    else if (!operations.contains(operation.toLowerCase()))
    	throw new UnsupportedException("The operation "+operation+" is not " +
    			"supported by the UDDI version 2 Inquiry API.");
	}
}
