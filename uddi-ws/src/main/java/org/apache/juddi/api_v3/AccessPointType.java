/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.api_v3;

/**
 * Provides a basic enumeration of the predefinied useType values for UDDI service/bindingTemplates/accessPoints/useType
 * @author unknown
 */
public enum AccessPointType {
	/**
         *  endPoint: designates that the accessPoint points to the actual service endpoint, i.e. the network address at which the Web service can be invoked,
         */
	END_POINT ("endPoint"),
        /**
         *  bindingTemplate: designates that the accessPoint contains a bindingKey that points to a different bindingTemplate entry. The value in providing this facility is seen when a business or entity wants to expose a service description (e.g. advertise that they have a service available that suits a specific purpose) that is actually a service that is described in a separate bindingTemplate record. This might occur when many service descriptions could benefit from a single service description,
         */
	BINDING_TEMPLATE("bindingTemplate"),
        /**
         * hostingRedirector: designates that the accessPoint can only be determined by querying another UDDI registry.  This might occur when a service is remotely hosted.
         */
	HOSTING_REDIRECTOR("hostingDirector"),
        /**
         * wsdlDeployment: designates that the accessPoint points to a remotely hosted WSDL document that already contains the necessary binding information, including the actual service endpoint.
         */
	WSDL_DEPLOYMENT ("wsdlDeployment");
	
	final String type;

	private AccessPointType(String type) {
		this.type = type;
	}
	
 	public String toString() {
		return type;	
	}
	
}
