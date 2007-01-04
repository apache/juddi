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

import org.apache.juddi.IRegistry;


/**
 * Base class for the request handler structures.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class AbstractHandler implements IHandler
{
	/** 
	 * Helper function to set the generic version number 
	 * to a default value when null or zero length.
	 * 
	 * @param generic version number
	 * @return generic version number
	 */
	public String getGeneric(String generic)
	{
		if ((generic == null) || (generic.trim().length() == 0))
	    {
		  // Default to UDDI v2 values
	      return IRegistry.UDDI_V2_GENERIC;
	    }
		return generic;
	}
	/**
	 * Helper function to get the namespace given the generic
	 * version number.
	 * 
	 * @param generic - the generic version number
	 * @return the namespace String
	 */
	public String getUDDINamespace(String generic)
	{
		if (IRegistry.UDDI_V1_GENERIC.equals(generic)) {
	        return IRegistry.UDDI_V1_NAMESPACE;
		} else if (IRegistry.UDDI_V2_GENERIC.equals(generic)) {
	        return IRegistry.UDDI_V2_NAMESPACE;
	    } else if (IRegistry.UDDI_V3_GENERIC.equals(generic)) {
	        return IRegistry.UDDI_V3_NAMESPACE;
	    } else {// Default to UDDI v2 values
	       return IRegistry.UDDI_V2_GENERIC;
	    }
	}
}
