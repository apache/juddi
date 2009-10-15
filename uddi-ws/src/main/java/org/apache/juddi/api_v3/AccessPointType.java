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

public enum AccessPointType {
	
	END_POINT ("endPoint"),
	BINDING_TEMPLATE("bindingTemplate"),
	HOSTING_REDIRECTOR("hostingDirector"),
	WSDL_DEPLOYMENT ("wsdlDeployment");
	
	final String type;

	private AccessPointType(String type) {
		this.type = type;
	}
	
 	public String toString() {
		return type;	
	}
	
}
